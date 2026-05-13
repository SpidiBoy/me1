package com.example.SpaMascotas.repository;

import com.example.SpaMascotas.model.Cita;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CitaRepository extends JpaRepository<Cita, Long> {
    List<Cita> findByFecha(LocalDate fecha);
    List<Cita> findByEstado(String estado);
    List<Cita> findByMascotaId(Long mascotaId);
    List<Cita> findByMascotaClienteId(Long clienteId);
    // Necesario para eliminar citas al borrar un servicio
    List<Cita> findByServiciosId(Long servicioId);
    long countByFechaAndEstadoNot(LocalDate fecha, String estado);
}
