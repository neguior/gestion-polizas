package co.com.segurosbolivar.polizas.controller;

import co.com.segurosbolivar.polizas.service.CoreIntegrationService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/core-mock")
public class CoreMockController {
    private final CoreIntegrationService coreService;
    public CoreMockController(CoreIntegrationService coreService) { this.coreService = coreService; }

    public record EventoRequest(@NotBlank String evento, @NotNull Long polizaId) {}
    public record EventoResponse(String mensaje, String evento, Long polizaId) {}

    @PostMapping("/evento")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public EventoResponse evento(@Valid @RequestBody EventoRequest request) {
        coreService.registrarIntento(request.evento(), request.polizaId());
        return new EventoResponse("Intento registrado en logs; no se invocó un CORE real",
                request.evento(), request.polizaId());
    }
}
