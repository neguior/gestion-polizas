# API de Gestión de Pólizas — Prueba técnica

Implementación de referencia para el módulo práctico de la prueba de Desarrollador TI.

## Stack
- Java 17
- Spring Boot 3.4.5
- Spring Web, Spring Data JPA, Bean Validation
- H2 en memoria
- Maven

## Requisitos
Instalar un JDK 17 y Maven 3.8+.

## Ejecutar
```bash
mvn spring-boot:run
```
La API inicia en `http://localhost:8080`. La base H2 en memoria se reinicia al detener la aplicación.

Todas las rutas, salvo la consola H2, exigen el header:
```http
x-api-key: 123456
```
La clave es únicamente de demostración; no debe usarse en producción.

## Configuración importante
`app.ipc-porcentaje=5.2` es un valor ilustrativo para probar renovación. Sustituirlo por el IPC oficial definido por el negocio o cargarlo desde un servicio/configuración administrada. La prima se calcula como canon mensual actualizado × meses de vigencia.

## Endpoints

| Método | Ruta | Propósito |
|---|---|---|
| GET | `/polizas?tipo=COLECTIVA&estado=ACTIVA` | Listar y filtrar pólizas |
| GET | `/polizas/{id}` | Consultar póliza |
| GET | `/polizas/{id}/riesgos` | Consultar riesgos |
| POST | `/polizas` | Crear póliza |
| POST | `/polizas/{id}/renovar` | Renovar póliza activa aplicando IPC configurado |
| POST | `/polizas/{id}/cancelar` | Cancelar póliza y todos sus riesgos |
| POST | `/polizas/{id}/riesgos` | Agregar riesgo (solo colectiva) |
| POST | `/riesgos/{id}/cancelar` | Cancelar riesgo |
| POST | `/core-mock/evento` | Registrar en logs un intento de envío al CORE |

## Ejemplos con curl

### Crear póliza colectiva
```bash
curl -X POST http://localhost:8080/polizas \
  -H "Content-Type: application/json" -H "x-api-key: 123456" \
  -d '{
    "tipo":"COLECTIVA",
    "canonMensual":1200000,
    "mesesVigencia":12,
    "fechaInicio":"2026-10-16",
    "fechaFin":"2027-10-15"
  }'
```

### Agregar riesgo
```bash
curl -X POST http://localhost:8080/polizas/1/riesgos \
  -H "Content-Type: application/json" -H "x-api-key: 123456" \
  -d '{"descripcion":"Apartamento 301","identificadorInmueble":"INM-301"}'
```

### Listar pólizas
```bash
curl "http://localhost:8080/polizas?tipo=COLECTIVA&estado=ACTIVA" -H "x-api-key: 123456"
```

### Renovar
```bash
curl -X POST http://localhost:8080/polizas/1/renovar -H "x-api-key: 123456"
```

### Cancelar póliza
```bash
curl -X POST http://localhost:8080/polizas/1/cancelar -H "x-api-key: 123456"
```

### Probar CORE mock
```bash
curl -X POST http://localhost:8080/core-mock/evento \
  -H "Content-Type: application/json" -H "x-api-key: 123456" \
  -d '{"evento":"ACTUALIZACION","polizaId":555}'
```

## Reglas de negocio implementadas
- La póliza individual no admite agregar riesgos por el endpoint de riesgos.
- No se renueva una póliza cancelada. La renovación de pólizas ya renovadas queda sujeta al control de vigencia y a las reglas temporales que defina el negocio.
- Cancelar una póliza cancela sus riesgos.
- Solo pólizas colectivas admiten agregar riesgos.
- Se validan datos requeridos y se devuelven errores HTTP coherentes.
- Las operaciones de mutación registran el intento de integración con CORE.

## Arquitectura y limitaciones conocidas
- Capas: controller → service → repository; entidades JPA y DTOs de entrada/salida.
- `CoreIntegrationService` es el adaptador/mock local: registra el intento en logs. En producción debe invocar el servicio agnóstico de edición disponible en la capa media/WebLogic. El endpoint `/core-mock/evento` permite probar el contrato de mock solicitado.
- La API key está codificada/configurada para la prueba. En producción usar secretos, TLS, rotación, identidad/autorización y auditoría.
- H2 es volátil y solo para demostración. En producción usar una base persistente administrada, migraciones, backups y estrategia de alta disponibilidad.
- Para una implementación productiva, añadir idempotencia, control de concurrencia, pruebas de integración/contrato, métricas, trazas distribuidas, outbox transaccional y envío asíncrono de notificaciones.
- La creación de póliza no crea automáticamente un riesgo: el enunciado práctico no especifica los campos necesarios para crear el riesgo inicial. Para una póliza individual, se debe definir el contrato de alta del riesgo con el negocio antes de producción.
- El evento CORE se registra en logs y no realiza una llamada HTTP real, tal como exige el mock de la prueba.

## Pruebas
```bash
mvn test
```
