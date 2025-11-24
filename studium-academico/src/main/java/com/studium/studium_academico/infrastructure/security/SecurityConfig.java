package com.studium.studium_academico.infrastructure.security;

import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    @Autowired
    SecurityFilter securityFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth

                        // ROTAS PÚBLICAS
                        .requestMatchers("/auth/login").permitAll()
                        .requestMatchers("/auth/activate").permitAll()
                        .requestMatchers("/api/admin/**").permitAll()
                        .requestMatchers("/api/admins/**").permitAll()


                        // DASHBOARD (qualquer usuário autenticado)
                        .requestMatchers("/api/dashboard/**").authenticated()


                        // ADMIN — Cria DIRETORES
                        .requestMatchers(HttpMethod.POST, "/api/admin/directors/**")
                        .hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/admin/directors/**")
                        .hasRole("ADMIN")


                        // DIRECTOR — Cria TEACHERS e STUDENTS
                        .requestMatchers(HttpMethod.POST, "/api/director/teachers/**")
                        .hasRole("DIRECTOR")
                        .requestMatchers(HttpMethod.POST, "/api/director/students/**")
                        .hasRole("DIRECTOR")

                        // Director pode ver lista de teachers/students
                        .requestMatchers(HttpMethod.GET, "/api/director/teachers/**")
                        .hasRole("DIRECTOR")
                        .requestMatchers(HttpMethod.GET, "/api/director/students/**")
                        .hasRole("DIRECTOR")


                        // PROFESSORES
                        // Teachers podem acessar alunos e grades/frequências
                        .requestMatchers(HttpMethod.GET, "/api/teachers/**")
                        .hasAnyRole("TEACHER", "DIRECTOR", "ADMIN")


                        // ALUNOS
                        .requestMatchers(HttpMethod.GET, "/api/students/**")
                        .hasAnyRole("TEACHER", "DIRECTOR", "ADMIN")


                        // Cursos
                        .requestMatchers(HttpMethod.GET, "/api/courses/**")
                        .hasAnyRole("STUDENT", "TEACHER", "DIRECTOR", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/courses/**")
                        .hasAnyRole("DIRECTOR", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/courses/**")
                        .hasAnyRole("DIRECTOR", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/courses/**")
                        .hasAnyRole("ADMIN")


                        // Frequências
                        .requestMatchers(HttpMethod.GET, "/api/frequencies/**")
                        .hasAnyRole("STUDENT", "TEACHER", "DIRECTOR", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/frequencies/**")
                        .hasAnyRole("TEACHER", "DIRECTOR", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/frequencies/**")
                        .hasAnyRole("TEACHER", "DIRECTOR", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/frequencies/**")
                        .hasRole("ADMIN")


                        // Notas
                        .requestMatchers(HttpMethod.GET, "/api/grades/**")
                        .hasAnyRole("STUDENT", "TEACHER", "DIRECTOR", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/grades/**")
                        .hasAnyRole("TEACHER", "DIRECTOR", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/grades/**")
                        .hasAnyRole("TEACHER", "DIRECTOR", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/grades/**")
                        .hasRole("ADMIN")


                        // Relatórios
                        .requestMatchers("/api/reports/**")
                        .hasAnyRole("TEACHER", "DIRECTOR", "ADMIN")


                        // Qualquer outra rota precisa estar autenticada
                        .anyRequest().authenticated()
                )
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
