package com.example.SpaMascotas.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Interceptor que protege todas las rutas del panel admin.
 * Si el usuario no está logueado, lo redirige a /login.
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        HttpSession session = request.getSession(false);

        if (session != null && session.getAttribute("logueado") != null) {
            return true; // sesión activa → continuar
        }

        // Sin sesión → redirigir al login
        response.sendRedirect(request.getContextPath() + "/login");
        return false;
    }
}
