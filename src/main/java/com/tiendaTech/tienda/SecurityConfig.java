package com.tiendaTech.tienda;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    // Rutas públicas
    public static final String[] PUBLIC_URLS = {
        "/", "/index", "/fav/**", "/carrito/**", "/consultas/**", "/registro/**",
        "/js/**", "/webjars/**", "/login", "/acceso_denegado"
    };

    // Rutas usuario
    public static final String[] USUARIO_URLS = {
        "/facturar/carrito"
    };

    // Rutas admin o vendedor
    public static final String[] ADMIN_OR_VENDEDOR_URLS = {
        "/producto/listado", "/categoria/listado", "/usuario/listado"
    };

    // Rutas admin
    public static final String[] ADMIN_URLS = {
        "/producto/**", "/categoria/**", "/usuario/**"
    };

    // 🔐 Configuración principal de seguridad
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            .authorizeHttpRequests(request -> request
                .requestMatchers(PUBLIC_URLS).permitAll()
                .requestMatchers(USUARIO_URLS).hasRole("USUARIO")
                .requestMatchers(ADMIN_OR_VENDEDOR_URLS).hasAnyRole("ADMIN", "VENDEDOR")
                .requestMatchers(ADMIN_URLS).hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/", true)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/")
                .permitAll()
            );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

   
    @Bean
    public UserDetailsService users(PasswordEncoder passwordEncoder) {

        UserDetails juan = User.builder()
                .username("juan")
                .password(passwordEncoder.encode("123"))
                .roles("ADMIN")
                .build();

        UserDetails rebeca = User.builder()
                .username("rebeca")
                .password(passwordEncoder.encode("456"))
                .roles("VENDEDOR")
                .build();

        UserDetails pedro = User.builder()
                .username("pedro")
                .password(passwordEncoder.encode("789"))
                .roles("USUARIO")
                .build();

        return new InMemoryUserDetailsManager(juan, rebeca, pedro);
    }

    
    @Autowired
    public void configurerGlobal(AuthenticationManagerBuilder build,
                                 PasswordEncoder passwordEncoder,
                                 UserDetailsService userDetailsService) throws Exception {

        build.userDetailsService(userDetailsService)
             .passwordEncoder(passwordEncoder);
    }
}