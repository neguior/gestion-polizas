package co.com.segurosbolivar.polizas.dto;

import co.com.segurosbolivar.polizas.model.*;
import java.time.LocalDate;

public record RiesgoResponse(Long id, String descripcion, String identificadorInmueble,
                             EstadoRiesgo estado, Long polizaId, LocalDate fechaCreacion) {
    public static RiesgoResponse from(Riesgo r) {
        return new RiesgoResponse(r.getId(), r.getDescripcion(), r.getIdentificadorInmueble(),
                r.getEstado(), r.getPoliza().getId(), r.getFechaCreacion());
    }
}
