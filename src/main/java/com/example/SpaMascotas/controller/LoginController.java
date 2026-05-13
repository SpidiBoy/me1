package com.example.SpaMascotas.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controlador de Login / Logout.
 *
 * GET /login → muestra el formulario
 * POST /login → valida credenciales y redirige al dashboard
 * GET /logout → cierra sesión y vuelve al login
 */
@Controller
public class LoginController {

    // Credenciales
    private static final String USUARIO_VALIDO = "harol@gmail.com";
    private static final String PASSWORD_VALIDO = "harol@gmail.com";

    /** Muestra el login. Si ya hay sesión activa, va directo al dashboard. */
    @GetMapping("/login")
    public String mostrarLogin(HttpSession session) {
        if (session.getAttribute("logueado") != null) {
            return "redirect:/";
        }
        return "login";
    }

    /** Procesa el formulario de login. */
    @PostMapping("/login")
    public String procesarLogin(
            @RequestParam String email,
            @RequestParam String password,
            HttpSession session,
            Model model) {

        if (USUARIO_VALIDO.equals(email) && PASSWORD_VALIDO.equals(password)) {
            session.setAttribute("logueado", true);
            session.setAttribute("usuario", email);
            return "redirect:/";
        }

        model.addAttribute("error", "Credenciales incorrectas ❌");
        return "login";
    }

    /** Cierra la sesión. */
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
