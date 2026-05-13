package com.example.SpaMascotas.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "citas")
public class Cita {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "mascota_id", nullable = false)
    private Mascota mascota;

    /**
     * Una cita puede incluir múltiples servicios (baño + corte + uñas, etc.)
     * Tabla intermedia: cita_servicios
     */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "cita_servicios",
        joinColumns = @JoinColumn(name = "cita_id"),
        inverseJoinColumns = @JoinColumn(name = "servicio_id")
    )
    private List<Servicio> servicios = new ArrayList<>();

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(nullable = false)
    private LocalTime hora;

    private String responsable;
    private String notas;

    // PENDIENTE, EN_CURSO, COMPLETADO, CANCELADO
    private String estado;
    private LocalDate fechaAnterior;
    private LocalTime horaAnterior;
    private String motivoReprogramacion;
    private Integer vecesReprogramada = 0;

    // ── Constructores ────────────────────────────────────────────────────────
    public Cita() {}

    public Cita(Mascota mascota, List<Servicio> servicios, LocalDate fecha,
                LocalTime hora, String responsable) {
        this.mascota    = mascota;
        this.servicios  = servicios != null ? servicios : new ArrayList<>();
        this.fecha      = fecha;
        this.hora       = hora;
        this.responsable = responsable;
        this.estado     = "PENDIENTE";
    }

    /** Constructor de compatibilidad para código que pasaba un solo servicio */
    public Cita(Mascota mascota, Servicio servicio, LocalDate fecha,
                LocalTime hora, String responsable) {
        this.mascota = mascota;
        if (servicio != null) this.servicios.add(servicio);
        this.fecha      = fecha;
        this.hora       = hora;
        this.responsable = responsable;
        this.estado     = "PENDIENTE";
    }

    // ── Helpers calculados ───────────────────────────────────────────────────

    /** Precio total sumando todos los servicios de la cita */
    @Transient
    public BigDecimal getTotalPrecio() {
        return servicios.stream()
                .map(s -> s.getPrecio() != null ? s.getPrecio() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /** Duración total en minutos */
    @Transient
    public int getTotalDuracion() {
        return servicios.stream()
                .mapToInt(s -> s.getDuracionMinutos() != null ? s.getDuracionMinutos() : 0)
                .sum();
    }

    /** Nombres de servicios separados por coma (útil en vistas) */
    @Transient
    public String getNombresServicios() {
        return servicios.stream()
                .map(Servicio::getNombre)
                .reduce((a, b) -> a + ", " + b)
                .orElse("—");
    }

    // ── Getters y Setters ────────────────────────────────────────────────────
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Mascota getMascota() { return mascota; }
    public void setMascota(Mascota mascota) { this.mascota = mascota; }

    public List<Servicio> getServicios() { return servicios; }
    public void setServicios(List<Servicio> servicios) {
        this.servicios = servicios != null ? servicios : new ArrayList<>();
    }

    /** Compatibilidad: devuelve el primer servicio si existe */
    @Transient
    public Servicio getServicio() {
        return servicios.isEmpty() ? null : servicios.get(0);
    }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public LocalTime getHora() { return hora; }
    public void setHora(LocalTime hora) { this.hora = hora; }

    public String getResponsable() { return responsable; }
    public void setResponsable(String responsable) { this.responsable = responsable; }

    public String getNotas() { return notas; }
    public void setNotas(String notas) { this.notas = notas; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public LocalDate getFechaAnterior() { return fechaAnterior; }
    public void setFechaAnterior(LocalDate fechaAnterior) { this.fechaAnterior = fechaAnterior; }

    public LocalTime getHoraAnterior() { return horaAnterior; }
    public void setHoraAnterior(LocalTime horaAnterior) { this.horaAnterior = horaAnterior; }

    public String getMotivoReprogramacion() { return motivoReprogramacion; }
    public void setMotivoReprogramacion(String motivoReprogramacion) {
        this.motivoReprogramacion = motivoReprogramacion;
    }

    public Integer getVecesReprogramada() { return vecesReprogramada; }
    public void setVecesReprogramada(Integer vecesReprogramada) {
        this.vecesReprogramada = vecesReprogramada;
    }
}
