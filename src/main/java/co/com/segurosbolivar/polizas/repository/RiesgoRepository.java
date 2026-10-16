
package co.com.segurosbolivar.polizas.repository;

import co.com.segurosbolivar.polizas.model.Riesgo;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RiesgoRepository extends JpaRepository<Riesgo, Long> {
    List<Riesgo> findByPolizaId(Long polizaId);

    boolean existsByPolizaIdAndIdentificadorInmueble(
            Long polizaId,
            String identificadorInmueble
    );
}
