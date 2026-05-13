package com.example.SpaMascotas.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.SpaMascotas.model.Cita;
import com.example.SpaMascotas.repository.CitaRepository;
import com.example.SpaMascotas.repository.ClienteRepository;
import com.example.SpaMascotas.repository.MascotaRepository;
import com.example.SpaMascotas.repository.ServicioRepository;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Controller
public class DashboardController {

    @Autowired private MascotaRepository  mascotaRepo;
    @Autowired private ClienteRepository  clienteRepo;
    @Autowired private CitaRepository     citaRepo;
    @Autowired private ServicioRepository servicioRepo;

    @GetMapping("/")
    public String dashboard(Model model) {
        LocalDate hoy = LocalDate.now();

        // Tarjetas de estadísticas
        model.addAttribute("totalMascotas",   mascotaRepo.count());
        model.addAttribute("totalClientes",   clienteRepo.count());
        model.addAttribute("citasHoy",        citaRepo.countByFechaAndEstadoNot(hoy, "CANCELADO"));
        model.addAttribute("citasPendientes", citaRepo.findByEstado("PENDIENTE").size());

        // Citas del día ordenadas por hora (incluye las del catálogo)
        List<Cita> citasDelDia = citaRepo.findByFecha(hoy);
        citasDelDia.sort(Comparator.comparing(Cita::getHora));
        model.addAttribute("citasDelDia", citasDelDia);

        return "dashboard";
    }
}
