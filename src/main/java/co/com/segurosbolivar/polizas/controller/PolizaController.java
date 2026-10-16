package co.com.segurosbolivar.polizas.controller;

import co.com.segurosbolivar.polizas.dto.*;
import co.com.segurosbolivar.polizas.model.*;
import co.com.segurosbolivar.polizas.service.PolizaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/polizas")
public class PolizaController {
    private final PolizaService service;

    public PolizaController(PolizaService service) { this.service = service; }

    @GetMapping
    public List<PolizaResponse> listar(@RequestParam(required = false) TipoPoliza tipo,
                                       @RequestParam(required = false) EstadoPoliza estado) {
        return service.listar(tipo, estado);
    }

    @GetMapping("/{id}")
    public PolizaResponse obtener(@PathVariable Long id) { return service.obtener(id); }

    @GetMapping("/{id}/riesgos")
    public List<RiesgoResponse> riesgos(@PathVariable Long id) { return service.listarRiesgos(id); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PolizaResponse crear(@Valid @RequestBody CrearPolizaRequest request) {
        return service.crear(request);
    }

    @PostMapping("/{id}/renovar")
    public PolizaResponse renovar(@PathVariable Long id) { return service.renovar(id); }

    @PostMapping("/{id}/cancelar")
    public PolizaResponse cancelar(@PathVariable Long id) { return service.cancelar(id); }

    @PostMapping("/{id}/riesgos")
    @ResponseStatus(HttpStatus.CREATED)
    public RiesgoResponse agregarRiesgo(@PathVariable Long id, @Valid @RequestBody CrearRiesgoRequest request) {
        return service.agregarRiesgo(id, request);
    }
}
