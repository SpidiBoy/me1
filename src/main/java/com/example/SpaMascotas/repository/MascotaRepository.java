package com.example.SpaMascotas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.SpaMascotas.model.Mascota;

import java.util.List;

@Repository
public interface MascotaRepository extends JpaRepository<Mascota, Long> {
    List<Mascota> findByEstado(String estado);

    List<Mascota> findByClienteId(Long clienteId);
}
