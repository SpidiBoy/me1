package com.example.SpaMascotas.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configura el interceptor de autenticación.
 *
 * Rutas protegidas: todo excepto /login, /logout y los recursos estáticos
 * (CSS, JS, imágenes) y la página pública /catalogo.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private AuthInterceptor authInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                // Protege todas las rutas del panel admin
                .addPathPatterns("/**")
                // Excluye login/logout
                .excludePathPatterns("/login", "/logout")
                // Excluye la página pública del catálogo
                .excludePathPatterns("/catalogo", "/catalogo.html")
                // Excluye recursos estáticos
                .excludePathPatterns("/css/**", "/js/**", "/images/**",
                                     "/imagenes/**", "/img/**", "/webjars/**",
                                     "/favicon.ico")
                // Excluye consola H2 (desarrollo)
                .excludePathPatterns("/h2-console/**")
                // Excluye el check de sesión (lo llama la página pública)
                .excludePathPatterns("/api/session-check");
    }
}
