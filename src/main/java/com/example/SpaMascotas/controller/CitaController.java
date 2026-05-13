package com.example.SpaMascotas.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.SpaMascotas.model.Cita;
import com.example.SpaMascotas.model.Servicio;
import com.example.SpaMascotas.model.Mascota;
import com.example.SpaMascotas.repository.CitaRepository;
import com.example.SpaMascotas.repository.MascotaRepository;
import com.example.SpaMascotas.repository.ServicioRepository;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Controlador de Citas.
 *
 * GET  /citas              → lista todas las citas
 * GET  /citas/nueva        → formulario nueva cita
 * POST /citas/nueva        → guarda nueva cita (múltiples servicios)
 * GET  /citas/{id}/editar  → formulario editar cita existente
 * POST /citas/{id}/editar  → actualiza cita existente
 * POST /citas/{id}/cancelar   → cancela
 * POST /citas/{id}/completar  → completa
 * POST /citas/{id}/iniciar    → inicia
 * GET  /citas/{id}/reprogramar → formulario reprogramar
 * POST /citas/{id}/reprogramar → guarda reprogramación
 */
@Controller
@RequestMapping("/citas")
public class CitaController {

    @Autowired private CitaRepository     citaRepo;
    @Autowired private MascotaRepository  mascotaRepo;
    @Autowired private ServicioRepository servicioRepo;

    // ── Listar ──────────────────────────────────────────────────────────────
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("citas", citaRepo.findAll());
        return "citas/lista";
    }

    // ── Formulario nueva cita ────────────────────────────────────────────────
    @GetMapping("/nueva")
    public String nuevaForm(Model model) {
        model.addAttribute("cita",      new Cita());
        model.addAttribute("mascotas",  mascotaRepo.findAll());
        model.addAttribute("servicios", servicioRepo.findAll());
        model.addAttribute("hoy",       LocalDate.now().toString()); // para min= en el input fecha
        return "citas/form";
    }

    // ── Guardar nueva cita ───────────────────────────────────────────────────
    @PostMapping("/nueva")
    public String guardar(
            @RequestParam("mascotaId")   Long        mascotaId,
            @RequestParam("servicioIds") List<Long>  servicioIds,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime hora,
            @RequestParam(required = false) String responsable,
            @RequestParam(required = false) String notas,
            Model model) {

        // Validar que la fecha no sea anterior a hoy
        if (fecha.isBefore(LocalDate.now())) {
            model.addAttribute("errorFecha", "La fecha no puede ser anterior a hoy.");
            model.addAttribute("cita",      new Cita());
            model.addAttribute("mascotas",  mascotaRepo.findAll());
            model.addAttribute("servicios", servicioRepo.findAll());
            model.addAttribute("hoy",       LocalDate.now().toString());
            return "citas/form";
        }

        Mascota mascota = mascotaRepo.findById(mascotaId)
                .orElseThrow(() -> new RuntimeException("Mascota no encontrada: " + mascotaId));

        List<Servicio> servicios = servicioRepo.findAllById(servicioIds);

        Cita cita = new Cita(mascota, servicios, fecha, hora, responsable);
        cita.setNotas(notas);
        cita.setEstado("PENDIENTE");
        citaRepo.save(cita);

        return "redirect:/citas";
    }

    // ── Formulario editar cita ───────────────────────────────────────────────
    @GetMapping("/{id}/editar")
    public String editarForm(@PathVariable Long id, Model model) {
        Cita cita = citaRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Cita no encontrada: " + id));

        // Solo se puede editar si está PENDIENTE o EN_CURSO
        if ("COMPLETADO".equals(cita.getEstado()) || "CANCELADO".equals(cita.getEstado())) {
            return "redirect:/citas";
        }

        // IDs de servicios actualmente seleccionados (para marcar checkboxes)
        List<Long> serviciosSeleccionados = cita.getServicios().stream()
                .map(s -> s.getId())
                .toList();

        model.addAttribute("cita",                  cita);
        model.addAttribute("mascotas",              mascotaRepo.findAll());
        model.addAttribute("servicios",             servicioRepo.findAll());
        model.addAttribute("serviciosSeleccionados", serviciosSeleccionados);
        model.addAttribute("hoy",                   LocalDate.now().toString());
        return "citas/editar";
    }

    // ── Guardar edición de cita ──────────────────────────────────────────────
    @PostMapping("/{id}/editar")
    public String actualizar(
            @PathVariable Long id,
            @RequestParam("mascotaId")   Long        mascotaId,
            @RequestParam("servicioIds") List<Long>  servicioIds,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime hora,
            @RequestParam(required = false) String responsable,
            @RequestParam(required = false) String notas,
            Model model) {

        Cita cita = citaRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Cita no encontrada: " + id));

        // Validar fecha
        if (fecha.isBefore(LocalDate.now())) {
            List<Long> serviciosSeleccionados = cita.getServicios().stream()
                    .map(s -> s.getId()).toList();
            model.addAttribute("errorFecha", "La fecha no puede ser anterior a hoy.");
            model.addAttribute("cita",                  cita);
            model.addAttribute("mascotas",              mascotaRepo.findAll());
            model.addAttribute("servicios",             servicioRepo.findAll());
            model.addAttribute("serviciosSeleccionados", serviciosSeleccionados);
            model.addAttribute("hoy",                   LocalDate.now().toString());
            return "citas/editar";
        }

        Mascota mascota = mascotaRepo.findById(mascotaId)
                .orElseThrow(() -> new RuntimeException("Mascota no encontrada: " + mascotaId));
        List<Servicio> servicios = servicioRepo.findAllById(servicioIds);

        cita.setMascota(mascota);
        cita.setServicios(servicios);
        cita.setFecha(fecha);
        cita.setHora(hora);
        cita.setResponsable(responsable);
        cita.setNotas(notas);

        citaRepo.save(cita);
        return "redirect:/citas";
    }

    // ── Cancelar ─────────────────────────────────────────────────────────────
    @PostMapping("/{id}/cancelar")
    public String cancelar(@PathVariable Long id) {
        Cita cita = citaRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Cita no encontrada: " + id));
        cita.setEstado("CANCELADO");
        citaRepo.save(cita);
        return "redirect:/citas";
    }

    // ── Completar ─────────────────────────────────────────────────────────────
    @PostMapping("/{id}/completar")
    public String completar(@PathVariable Long id) {
        Cita cita = citaRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Cita no encontrada: " + id));
        cita.setEstado("COMPLETADO");
        citaRepo.save(cita);
        return "redirect:/citas";
    }

    // ── Iniciar ───────────────────────────────────────────────────────────────
    @PostMapping("/{id}/iniciar")
    public String iniciar(@PathVariable Long id) {
        Cita cita = citaRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Cita no encontrada: " + id));
        cita.setEstado("EN_CURSO");
        citaRepo.save(cita);
        return "redirect:/citas";
    }

    // ── Reprogramar (formulario) ──────────────────────────────────────────────
    @GetMapping("/{id}/reprogramar")
    public String reprogramarForm(@PathVariable Long id, Model model) {
        Cita cita = citaRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Cita no encontrada: " + id));
        if ("COMPLETADO".equals(cita.getEstado()) || "CANCELADO".equals(cita.getEstado())) {
            return "redirect:/citas";
        }
        model.addAttribute("cita", cita);
        model.addAttribute("hoy",  LocalDate.now().toString());
        return "citas/reprogramar";
    }

    // ── Reprogramar (guardar) ─────────────────────────────────────────────────
    @PostMapping("/{id}/reprogramar")
    public String reprogramar(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate nuevaFecha,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime nuevaHora,
            @RequestParam(required = false) String motivo,
            Model model) {

        Cita cita = citaRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Cita no encontrada: " + id));

        // Validar fecha
        if (nuevaFecha.isBefore(LocalDate.now())) {
            model.addAttribute("errorFecha", "La nueva fecha no puede ser anterior a hoy.");
            model.addAttribute("cita", cita);
            model.addAttribute("hoy",  LocalDate.now().toString());
            return "citas/reprogramar";
        }

        cita.setFechaAnterior(cita.getFecha());
        cita.setHoraAnterior(cita.getHora());
        cita.setMotivoReprogramacion(motivo);
        cita.setVecesReprogramada(
            cita.getVecesReprogramada() == null ? 1 : cita.getVecesReprogramada() + 1
        );
        cita.setFecha(nuevaFecha);
        cita.setHora(nuevaHora);
        cita.setEstado("PENDIENTE");

        citaRepo.save(cita);
        return "redirect:/citas";
    }
}