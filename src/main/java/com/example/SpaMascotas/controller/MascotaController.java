package com.example.SpaMascotas.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.example.SpaMascotas.model.Cliente;
import com.example.SpaMascotas.model.Mascota;
import com.example.SpaMascotas.repository.CitaRepository;
import com.example.SpaMascotas.repository.ClienteRepository;
import com.example.SpaMascotas.repository.MascotaRepository;

@Controller
@RequestMapping("/mascotas")
public class MascotaController {

    @Autowired
    private MascotaRepository mascotaRepo;

    @Autowired
    private ClienteRepository clienteRepo;

    @Autowired
    private CitaRepository citaRepo;

    // ── Listar ──────────────────────────────────────────────────────────────
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("mascotas", mascotaRepo.findAll());
        return "mascotas/lista";
    }

    // ── Formulario nueva mascota ─────────────────────────────────────────────
    // Acepta ?clienteId=X opcional (viene del botón "Agregar mascota" en el cliente)
    @GetMapping("/nueva")
    public String nuevaForm(
            @RequestParam(required = false) Long clienteId,
            Model model) {

        model.addAttribute("mascota", new Mascota());
        model.addAttribute("clientes", clienteRepo.findAll());

        // Si viene con clienteId preseleccionado, lo pasamos a la vista
        if (clienteId != null) {
            model.addAttribute("clientePreseleccionado", clienteId);
        }

        return "mascotas/form";
    }

    // ── Guardar nueva mascota ────────────────────────────────────────────────
    // Recibe clienteId como campo del formulario (no como @ModelAttribute de Mascota)
    @PostMapping("/nueva")
    public String guardar(
            @ModelAttribute Mascota mascota,
            @RequestParam(required = false) Long clienteId) {

        // Vincular el cliente manualmente
        if (clienteId != null) {
            Cliente cliente = clienteRepo.findById(clienteId)
                    .orElseThrow(() -> new RuntimeException("Cliente no encontrado: " + clienteId));
            mascota.setCliente(cliente);
        }

        mascota.setEstado("ACTIVO");
        mascotaRepo.save(mascota);
        return "redirect:/mascotas";
    }

    // ── Formulario editar mascota ────────────────────────────────────────────
    @GetMapping("/{id}/editar")
    public String editarForm(@PathVariable Long id, Model model) {
        Mascota mascota = mascotaRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Mascota no encontrada: " + id));
        model.addAttribute("mascota", mascota);
        model.addAttribute("clientes", clienteRepo.findAll());

        // Preseleccionar el cliente actual de la mascota
        if (mascota.getCliente() != null) {
            model.addAttribute("clientePreseleccionado", mascota.getCliente().getId());
        }

        return "mascotas/form";
    }

    // ── Actualizar mascota ───────────────────────────────────────────────────
    @PostMapping("/{id}/editar")
    public String actualizar(
            @PathVariable Long id,
            @ModelAttribute Mascota mascota,
            @RequestParam(required = false) Long clienteId) {

        mascota.setId(id);

        // Recuperar el estado anterior para no perderlo al editar
        mascotaRepo.findById(id).ifPresent(existente -> {
            mascota.setEstado(existente.getEstado());
            mascota.setFechaRegistro(existente.getFechaRegistro());
            mascota.setUltimaVisita(existente.getUltimaVisita());
        });

        // Vincular cliente
        if (clienteId != null) {
            Cliente cliente = clienteRepo.findById(clienteId)
                    .orElseThrow(() -> new RuntimeException("Cliente no encontrado: " + clienteId));
            mascota.setCliente(cliente);
        }

        mascotaRepo.save(mascota);
        return "redirect:/mascotas";
    }

    // ── Eliminar mascota ─────────────────────────────────────────────────────
    /**
     * Elimina la mascota junto con todas sus citas asociadas,
     * evitando la violación de FK constraint.
     */
    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Long id) {
        citaRepo.deleteAll(citaRepo.findByMascotaId(id));
        mascotaRepo.deleteById(id);
        return "redirect:/mascotas";
    }
}
