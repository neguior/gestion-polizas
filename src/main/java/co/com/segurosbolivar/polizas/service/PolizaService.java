package co.com.segurosbolivar.polizas.service;

import co.com.segurosbolivar.polizas.dto.*;
import co.com.segurosbolivar.polizas.model.*;
import co.com.segurosbolivar.polizas.repository.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class PolizaService {
    private final PolizaRepository polizaRepository;
    private final RiesgoRepository riesgoRepository;
    private final CoreIntegrationService coreIntegrationService;
    private final BigDecimal ipcPorcentaje;

    public PolizaService(PolizaRepository polizaRepository, RiesgoRepository riesgoRepository,
                         CoreIntegrationService coreIntegrationService,
                         @Value("${app.ipc-porcentaje:5.2}") BigDecimal ipcPorcentaje) {
        this.polizaRepository = polizaRepository;
        this.riesgoRepository = riesgoRepository;
        this.coreIntegrationService = coreIntegrationService;
        this.ipcPorcentaje = ipcPorcentaje;
    }

    @Transactional(readOnly = true)
    public List<PolizaResponse> listar(TipoPoliza tipo, EstadoPoliza estado) {
        List<Poliza> polizas;
        if (tipo != null && estado != null) polizas = polizaRepository.findByTipoAndEstado(tipo, estado);
        else if (tipo != null) polizas = polizaRepository.findByTipo(tipo);
        else if (estado != null) polizas = polizaRepository.findByEstado(estado);
        else polizas = polizaRepository.findAll();
        return polizas.stream().map(PolizaResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<RiesgoResponse> listarRiesgos(Long polizaId) {
        if (!polizaRepository.existsById(polizaId)) throw notFound("Póliza", polizaId);
        return riesgoRepository.findByPolizaId(polizaId).stream().map(RiesgoResponse::from).toList();
    }

    
    @Transactional
    public PolizaResponse crear(CrearPolizaRequest request) {
        if (request.fechaFin().isBefore(request.fechaInicio())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "fechaFin debe ser igual o posterior a fechaInicio"
            );
        }

        if (request.tipo() == TipoPoliza.INDIVIDUAL) {
            if (request.riesgoInicialDescripcion() == null
                    || request.riesgoInicialDescripcion().isBlank()
                    || request.riesgoInicialIdentificadorInmueble() == null
                    || request.riesgoInicialIdentificadorInmueble().isBlank()) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Las pólizas individuales requieren un riesgo inicial"
                );
            }
        }

        Poliza poliza = new Poliza(
                request.tipo(),
                request.canonMensual(),
                request.mesesVigencia(),
                request.fechaInicio(),
                request.fechaFin()
        );

        if (request.tipo() == TipoPoliza.INDIVIDUAL) {
            Riesgo riesgo = new Riesgo(
                    request.riesgoInicialDescripcion(),
                    request.riesgoInicialIdentificadorInmueble()
            );
            poliza.agregarRiesgo(riesgo);
        }

        Poliza guardada = polizaRepository.save(poliza);

        coreIntegrationService.registrarIntento(
                "CREACION",
                guardada.getId()
        );

        return PolizaResponse.from(guardada);
    }


    @Transactional
    public PolizaResponse renovar(Long id) {
        Poliza poliza = obtenerPoliza(id);
        if (poliza.getEstado() == EstadoPoliza.CANCELADA) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "No se puede renovar una póliza cancelada");
        }
        poliza.renovar(ipcPorcentaje);
        coreIntegrationService.registrarIntento("RENOVACION", id);
        return PolizaResponse.from(polizaRepository.save(poliza));
    }

    @Transactional
    public PolizaResponse cancelar(Long id) {
        Poliza poliza = obtenerPoliza(id);
        if (poliza.getEstado() == EstadoPoliza.CANCELADA) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "La póliza ya está cancelada");
        }
        poliza.cancelar();
        poliza.getRiesgos().forEach(Riesgo::cancelar);
        coreIntegrationService.registrarIntento("CANCELACION", id);
        return PolizaResponse.from(polizaRepository.save(poliza));
    }

@Transactional
public RiesgoResponse agregarRiesgo(Long polizaId, CrearRiesgoRequest request) {
    Poliza poliza = obtenerPoliza(polizaId);

    if (poliza.getTipo() != TipoPoliza.COLECTIVA) {
        throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Solo las pólizas colectivas admiten varios riesgos"
        );
    }

    if (poliza.getEstado() == EstadoPoliza.CANCELADA) {
        throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "No se pueden agregar riesgos a una póliza cancelada"
        );
    }

    if (riesgoRepository.existsByPolizaIdAndIdentificadorInmueble(
            polizaId,
            request.identificadorInmueble())) {
        throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "Ya existe un riesgo con ese identificador en esta póliza"
        );
    }

    Riesgo riesgo = new Riesgo(
            request.descripcion(),
            request.identificadorInmueble()
    );

    poliza.agregarRiesgo(riesgo);

    // Guardar el riesgo una sola vez.
    Riesgo guardado = riesgoRepository.save(riesgo);

    coreIntegrationService.registrarIntento(
            "AGREGAR_RIESGO",
            polizaId
    );

    return RiesgoResponse.from(guardado);
}
    @Transactional
    public RiesgoResponse cancelarRiesgo(Long riesgoId) {
        Riesgo riesgo = riesgoRepository.findById(riesgoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Riesgo no encontrado: " + riesgoId));
        if (riesgo.getEstado() == EstadoRiesgo.CANCELADO) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El riesgo ya está cancelado");
        }
        riesgo.cancelar();
        coreIntegrationService.registrarIntento("CANCELAR_RIESGO", riesgo.getPoliza().getId());
        return RiesgoResponse.from(riesgoRepository.save(riesgo));
    }

    @Transactional(readOnly = true)
    public PolizaResponse obtener(Long id) {
        return PolizaResponse.from(obtenerPoliza(id));
    }

    private Poliza obtenerPoliza(Long id) {
        return polizaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Póliza no encontrada: " + id));
    }

    private ResponseStatusException notFound(String tipo, Long id) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, tipo + " no encontrada: " + id);
    }
}
