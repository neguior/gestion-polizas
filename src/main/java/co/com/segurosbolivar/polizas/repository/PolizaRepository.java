package co.com.segurosbolivar.polizas.repository;

import co.com.segurosbolivar.polizas.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PolizaRepository extends JpaRepository<Poliza, Long> {
    List<Poliza> findByTipoAndEstado(TipoPoliza tipo, EstadoPoliza estado);
    List<Poliza> findByTipo(TipoPoliza tipo);
    List<Poliza> findByEstado(EstadoPoliza estado);
}
