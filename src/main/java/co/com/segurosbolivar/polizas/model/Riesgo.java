package co.com.segurosbolivar.polizas.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "riesgos")
public class Riesgo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String descripcion;

    @Column(nullable = false, length = 120)
    private String identificadorInmueble;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoRiesgo estado = EstadoRiesgo.ACTIVO;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "poliza_id", nullable = false)
    private Poliza poliza;

    @Column(nullable = false)
    private LocalDate fechaCreacion = LocalDate.now();

    protected Riesgo() {}

    public Riesgo(String descripcion, String identificadorInmueble) {
        this.descripcion = descripcion;
        this.identificadorInmueble = identificadorInmueble;
    }

    public Long getId() { return id; }
    public String getDescripcion() { return descripcion; }
    public String getIdentificadorInmueble() { return identificadorInmueble; }
    public EstadoRiesgo getEstado() { return estado; }
    public Poliza getPoliza() { return poliza; }
    public LocalDate getFechaCreacion() { return fechaCreacion; }
    public void setPoliza(Poliza poliza) { this.poliza = poliza; }
    public void cancelar() { this.estado = EstadoRiesgo.CANCELADO; }
}
