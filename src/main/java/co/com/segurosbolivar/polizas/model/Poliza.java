package co.com.segurosbolivar.polizas.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "polizas", indexes = {
        @Index(name = "idx_poliza_tipo_estado", columnList = "tipo,estado")
})
public class Poliza {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoPoliza tipo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoPoliza estado = EstadoPoliza.ACTIVA;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal canonMensual;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal prima;

    @Column(nullable = false)
    private Integer mesesVigencia;

    @Column(nullable = false)
    private LocalDate fechaInicio;

    @Column(nullable = false)
    private LocalDate fechaFin;

    @OneToMany(mappedBy = "poliza", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Riesgo> riesgos = new ArrayList<>();

    protected Poliza() {}

    public Poliza(TipoPoliza tipo, BigDecimal canonMensual, Integer mesesVigencia,
                  LocalDate fechaInicio, LocalDate fechaFin) {
        this.tipo = tipo;
        this.canonMensual = canonMensual;
        this.mesesVigencia = mesesVigencia;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.prima = canonMensual.multiply(BigDecimal.valueOf(mesesVigencia));
    }

    public void agregarRiesgo(Riesgo riesgo) {
        riesgos.add(riesgo);
        riesgo.setPoliza(this);
    }

    public Long getId() { return id; }
    public TipoPoliza getTipo() { return tipo; }
    public EstadoPoliza getEstado() { return estado; }
    public BigDecimal getCanonMensual() { return canonMensual; }
    public BigDecimal getPrima() { return prima; }
    public Integer getMesesVigencia() { return mesesVigencia; }
    public LocalDate getFechaInicio() { return fechaInicio; }
    public LocalDate getFechaFin() { return fechaFin; }
    public List<Riesgo> getRiesgos() { return riesgos; }

    public void renovar(BigDecimal ipcPorcentaje) {
        BigDecimal factor = BigDecimal.ONE.add(ipcPorcentaje.divide(BigDecimal.valueOf(100), 8, java.math.RoundingMode.HALF_UP));
        this.canonMensual = this.canonMensual.multiply(factor).setScale(2, java.math.RoundingMode.HALF_UP);
        this.prima = this.canonMensual.multiply(BigDecimal.valueOf(mesesVigencia)).setScale(2, java.math.RoundingMode.HALF_UP);
        this.fechaInicio = this.fechaFin.plusDays(1);
        this.fechaFin = this.fechaInicio.plusMonths(mesesVigencia).minusDays(1);
        this.estado = EstadoPoliza.RENOVADA;
    }

    public void cancelar() { this.estado = EstadoPoliza.CANCELADA; }
}
