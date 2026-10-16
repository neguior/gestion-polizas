
package co.com.segurosbolivar.polizas.dto;

import co.com.segurosbolivar.polizas.model.TipoPoliza;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public record CrearPolizaRequest(
        @NotNull TipoPoliza tipo,
        @NotNull @DecimalMin(value = "0.01") BigDecimal canonMensual,
        @NotNull @Min(1) @Max(60) Integer mesesVigencia,
        @NotNull LocalDate fechaInicio,
        @NotNull LocalDate fechaFin,
        @Size(max = 120) String riesgoInicialDescripcion,
        @Size(max = 120) String riesgoInicialIdentificadorInmueble
) {}
