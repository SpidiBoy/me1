package com.example.SpaMascotas.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * API REST liviana para que el catálogo (página pública) pueda saber
 * si el usuario actual tiene sesión activa, sin redireccionar.
 *
 * GET /api/session-check → { "logueado": true | false }
 */
@RestController
public class SessionCheckController {

    @GetMapping("/api/session-check")
    public Map<String, Boolean> checkSession(HttpSession session) {
        boolean logueado = session != null
                && session.getAttribute("logueado") != null;
        return Map.of("logueado", logueado);
    }
}
