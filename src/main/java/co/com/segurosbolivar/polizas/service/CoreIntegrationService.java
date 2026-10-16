package co.com.segurosbolivar.polizas.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Adaptador de demostración. En producción invocaría el servicio agnóstico
 * de edición expuesto por la capa media/WebLogic, con timeout, autenticación
 * de servicio, trazabilidad y política de reintentos controlada.
 */
@Service
public class CoreIntegrationService {
    private static final Logger log = LoggerFactory.getLogger(CoreIntegrationService.class);

    public void registrarIntento(String evento, Long polizaId) {
        log.info("CORE-MOCK: intento de envío evento={} polizaId={}", evento, polizaId);
    }
}
