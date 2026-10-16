package co.com.segurosbolivar.polizas.dto;

import co.com.segurosbolivar.polizas.model.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public record PolizaResponse(Long id, TipoPoliza tipo, EstadoPoliza estado,
                             BigDecimal canonMensual, BigDecimal prima, Integer mesesVigencia,
                             LocalDate fechaInicio, LocalDate fechaFin, int cantidadRiesgos) {
    public static PolizaResponse from(Poliza p) {
        return new PolizaResponse(p.getId(), p.getTipo(), p.getEstado(), p.getCanonMensual(),
                p.getPrima(), p.getMesesVigencia(), p.getFechaInicio(), p.getFechaFin(),
                p.getRiesgos().size());
    }
}
