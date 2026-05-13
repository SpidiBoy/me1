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

import java.util.List;

@Controller
@RequestMapping("/clientes")
public class ClienteController {

    @Autowired
    private ClienteRepository clienteRepo;

    @Autowired
    private MascotaRepository mascotaRepo;

    @Autowired
    private CitaRepository citaRepo;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("clientes", clienteRepo.findAll());
        return "clientes/lista";
    }

    @GetMapping("/nuevo")
    public String nuevoForm(Model model) {
        model.addAttribute("cliente", new Cliente());
        return "clientes/form";
    }

    @PostMapping("/nuevo")
    public String guardar(@ModelAttribute Cliente cliente) {
        clienteRepo.save(cliente);
        return "redirect:/clientes";
    }

    // ── Editar ───────────────────────────────────────────────────────────────
    @GetMapping("/{id}/editar")
    public String editarForm(@PathVariable Long id, Model model) {
        Cliente cliente = clienteRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado: " + id));
        model.addAttribute("cliente", cliente);
        return "clientes/form";
    }

    @PostMapping("/{id}/editar")
    public String actualizar(@PathVariable Long id, @ModelAttribute Cliente cliente) {
        cliente.setId(id);
        clienteRepo.save(cliente);
        return "redirect:/clientes";
    }

    // ── Detalle ──────────────────────────────────────────────────────────────
    @GetMapping("/{id}")
    public String detalle(@PathVariable Long id, Model model) {
        Cliente cliente = clienteRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado: " + id));
        model.addAttribute("cliente", cliente);
        model.addAttribute("citas", citaRepo.findByMascotaClienteId(id));
        return "clientes/detalle";
    }

    /**
     * Elimina el cliente en cascada:
     *  1. Citas de cada mascota
     *  2. Mascotas
     *  3. Cliente
     */
    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Long id) {
        List<Mascota> mascotas = mascotaRepo.findByClienteId(id);
        for (Mascota m : mascotas) {
            citaRepo.deleteAll(citaRepo.findByMascotaId(m.getId()));
        }
        mascotaRepo.deleteAll(mascotas);
        clienteRepo.deleteById(id);
        return "redirect:/clientes";
    }
}
