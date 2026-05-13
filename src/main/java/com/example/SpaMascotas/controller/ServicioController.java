package com.example.SpaMascotas.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.example.SpaMascotas.model.Servicio;
import com.example.SpaMascotas.repository.CitaRepository;
import com.example.SpaMascotas.repository.ServicioRepository;

@Controller
@RequestMapping("/servicios")
public class ServicioController {

    @Autowired
    private ServicioRepository servicioRepo;

    @Autowired
    private CitaRepository citaRepo;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("servicios", servicioRepo.findAll());
        return "servicios/lista";
    }

    @GetMapping("/nuevo")
    public String nuevoForm(Model model) {
        model.addAttribute("servicio", new Servicio());
        return "servicios/form";
    }

    @PostMapping("/nuevo")
    public String guardar(@ModelAttribute Servicio servicio) {
        servicioRepo.save(servicio);
        return "redirect:/servicios";
    }

    @GetMapping("/{id}/editar")
    public String editarForm(@PathVariable Long id, Model model) {
        Servicio servicio = servicioRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ID inválido: " + id));
        model.addAttribute("servicio", servicio);
        return "servicios/form";
    }

    @PostMapping("/{id}/editar")
    public String actualizar(@PathVariable Long id, @ModelAttribute Servicio servicio) {
        servicio.setId(id);
        servicioRepo.save(servicio);
        return "redirect:/servicios";
    }

    /**
     * Elimina el servicio junto con todas sus citas asociadas,
     * evitando la violación de FK constraint.
     */
    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Long id) {
        // 1. Eliminar primero todas las citas que usen este servicio
        citaRepo.deleteAll(citaRepo.findByServiciosId(id));
        // 2. Ahora sí eliminar el servicio
        servicioRepo.deleteById(id);
        return "redirect:/servicios";
    }
}
