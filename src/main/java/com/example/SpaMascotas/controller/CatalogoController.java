package com.example.SpaMascotas.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import com.example.SpaMascotas.repository.ServicioRepository;
import com.example.SpaMascotas.repository.ClienteRepository;
import com.example.SpaMascotas.repository.MascotaRepository;
import com.example.SpaMascotas.repository.CitaRepository;
import com.example.SpaMascotas.model.Cliente;
import com.example.SpaMascotas.model.Mascota;
import com.example.SpaMascotas.model.Servicio;
import com.example.SpaMascotas.model.Cita;

@Controller
public class CatalogoController {

    @Autowired private ServicioRepository servicioRepo;
    @Autowired private ClienteRepository  clienteRepo;
    @Autowired private MascotaRepository  mascotaRepo;
    @Autowired private CitaRepository     citaRepo;

    @GetMapping("/catalogo")
    public String catalogo(Model model) {
        model.addAttribute("servicios", servicioRepo.findAll());
        model.addAttribute("hoy", LocalDate.now().toString());
        return "catalogo";
    }

    @PostMapping("/catalogo/reservar")
    public String reservarCita(
            @RequestParam("nombreMascota")   String     nombreMascota,
            @RequestParam("especie")         String     especie,
            @RequestParam(value = "raza",    required = false) String raza,
            @RequestParam(value = "tamano",  required = false) String tamano,
            @RequestParam("servicioIds")     List<Long> servicioIds,   // ← múltiples
            @RequestParam("fecha")
                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            @RequestParam("hora")
                @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime hora,
            @RequestParam("nombreCliente")   String     nombreCliente,
            @RequestParam("telefono")        String     telefono,
            @RequestParam(value = "notas",   required = false) String notas,
            RedirectAttributes redirectAttributes) {

        // Validar fecha no anterior a hoy
        if (fecha.isBefore(LocalDate.now())) {
            redirectAttributes.addFlashAttribute("mensajeError",
                    "La fecha de la cita no puede ser anterior a hoy.");
            return "redirect:/catalogo";
        }

        // 1. Buscar o crear cliente por teléfono
        Cliente cliente = clienteRepo.findByTelefono(telefono).orElse(null);
        if (cliente == null) {
            cliente = new Cliente(nombreCliente, null, telefono);
            cliente = clienteRepo.save(cliente);
        }

        // 2. Crear mascota
        Mascota mascota = new Mascota(nombreMascota, especie, raza, null, null, cliente);
        mascota.setTamano(tamano);
        mascota = mascotaRepo.save(mascota);

        // 3. Obtener servicios seleccionados
        List<Servicio> servicios = servicioRepo.findAllById(servicioIds);

        // 4. Crear cita
        Cita cita = new Cita(mascota, servicios, fecha, hora, nombreCliente);
        cita.setNotas(notas);
        cita.setEstado("PENDIENTE");
        citaRepo.save(cita);

        redirectAttributes.addFlashAttribute("mensajeExito",
                "¡Reserva recibida! 🐾 Nos comunicaremos contigo pronto para confirmar tu cita.");
        return "redirect:/catalogo";
    }
}