package co.com.segurosbolivar.polizas.controller;

import co.com.segurosbolivar.polizas.dto.RiesgoResponse;
import co.com.segurosbolivar.polizas.service.PolizaService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/riesgos")
public class RiesgoController {
    private final PolizaService service;
    public RiesgoController(PolizaService service) { this.service = service; }

    @PostMapping("/{id}/cancelar")
    public RiesgoResponse cancelar(@PathVariable Long id) { return service.cancelarRiesgo(id); }
}
