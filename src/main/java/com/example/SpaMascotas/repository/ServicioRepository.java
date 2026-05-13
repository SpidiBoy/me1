package com.example.SpaMascotas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.SpaMascotas.model.Servicio;

@Repository
public interface ServicioRepository extends JpaRepository<Servicio, Long> {
}
