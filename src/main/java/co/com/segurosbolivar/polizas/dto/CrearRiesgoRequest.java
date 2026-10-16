package co.com.segurosbolivar.polizas.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CrearRiesgoRequest(
        @NotBlank @Size(max = 120) String descripcion,
        @NotBlank @Size(max = 120) String identificadorInmueble
) {}
