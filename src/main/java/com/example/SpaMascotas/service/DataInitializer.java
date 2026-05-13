package com.example.SpaMascotas.service;

import com.example.SpaMascotas.model.Cita;
import com.example.SpaMascotas.model.Cliente;
import com.example.SpaMascotas.model.Mascota;
import com.example.SpaMascotas.model.Servicio;
import com.example.SpaMascotas.repository.CitaRepository;
import com.example.SpaMascotas.repository.ClienteRepository;
import com.example.SpaMascotas.repository.MascotaRepository;
import com.example.SpaMascotas.repository.ServicioRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Inicializa datos de prueba SOLO si la base de datos está vacía.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired private ClienteRepository  clienteRepo;
    @Autowired private MascotaRepository  mascotaRepo;
    @Autowired private ServicioRepository servicioRepo;
    @Autowired private CitaRepository     citaRepo;

    @Override
    public void run(String... args) {

        if (servicioRepo.count() > 0) {
            System.out.println("✔ Datos ya existentes — DataInitializer omitido.");
            return;
        }

        // ── Servicios ──────────────────────────────────────────────────────
        Servicio bano   = servicioRepo.save(new Servicio("Baño completo",   "Baño, secado y perfumado",       45, new BigDecimal("45.00")));
        Servicio corte  = servicioRepo.save(new Servicio("Corte de pelo",   "Corte estilo y acabado",         60, new BigDecimal("60.00")));
        Servicio unias  = servicioRepo.save(new Servicio("Corte de uñas",   "Limado y recorte",               20, new BigDecimal("20.00")));
        Servicio spa    = servicioRepo.save(new Servicio("Spa completo",    "Baño + Corte + Uñas + Masaje",   90, new BigDecimal("110.00")));
        Servicio dental = servicioRepo.save(new Servicio("Limpieza dental", "Higiene bucal profesional",      30, new BigDecimal("35.00")));

        bano.setIcono("🛁");    servicioRepo.save(bano);
        corte.setIcono("✂️");   servicioRepo.save(corte);
        unias.setIcono("💅");   servicioRepo.save(unias);
        spa.setIcono("⭐");     servicioRepo.save(spa);
        dental.setIcono("🦷");  servicioRepo.save(dental);

        // ── Clientes ───────────────────────────────────────────────────────
        Cliente ana    = clienteRepo.save(new Cliente("Ana García",   "ana@gmail.com",    "987-123-456"));
        Cliente carlos = clienteRepo.save(new Cliente("Carlos Pérez", "cperez@mail.com",  "987-654-321"));
        Cliente maria  = clienteRepo.save(new Cliente("María López",  "mlopez@mail.com",  "912-345-678"));
        Cliente luis   = clienteRepo.save(new Cliente("Luis Torres",  "ltorres@mail.com", "934-567-890"));

        // ── Mascotas ───────────────────────────────────────────────────────
        Mascota mochi = mascotaRepo.save(new Mascota("Mochi", "Perro", "Pomerania",        2, "Hembra", ana));
        Mascota luna  = mascotaRepo.save(new Mascota("Luna",  "Gato",  "Siamés",           3, "Hembra", carlos));
        Mascota rocky = mascotaRepo.save(new Mascota("Rocky", "Perro", "Golden Retriever", 4, "Macho",  maria));
        Mascota coco  = mascotaRepo.save(new Mascota("Coco",  "Conejo","Angora",           1, "Macho",  luis));

        mochi.setUltimaVisita(LocalDate.now().minusDays(5));
        luna.setUltimaVisita(LocalDate.now().minusDays(8));
        rocky.setUltimaVisita(LocalDate.now().minusDays(12));
        mascotaRepo.save(mochi);
        mascotaRepo.save(luna);
        mascotaRepo.save(rocky);

        // ── Citas de hoy (múltiples servicios) ────────────────────────────
        // Mochi: baño + corte
        Cita c1 = new Cita(mochi, List.of(bano, corte), LocalDate.now(), LocalTime.of(10, 0), "Sofía R.");

        // Luna: solo corte de uñas
        Cita c2 = new Cita(luna, List.of(unias), LocalDate.now(), LocalTime.of(11, 30), "Javier M.");
        c2.setEstado("EN_CURSO");

        // Rocky: spa completo
        Cita c3 = new Cita(rocky, List.of(spa), LocalDate.now(), LocalTime.of(14, 0), "Sofía R.");

        // Coco: ayer, completado
        Cita c4 = new Cita(coco, List.of(bano), LocalDate.now().minusDays(1), LocalTime.of(9, 0), "Javier M.");
        c4.setEstado("COMPLETADO");

        citaRepo.save(c1);
        citaRepo.save(c2);
        citaRepo.save(c3);
        citaRepo.save(c4);

        System.out.println("✔ Datos de prueba cargados exitosamente.");
    }
}