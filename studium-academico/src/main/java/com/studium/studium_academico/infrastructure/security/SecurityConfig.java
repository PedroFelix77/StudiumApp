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
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> {})
                .sessionManagement(sm ->
                        sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth

                        // AUTH
                        .requestMatchers("/api/auth/**").permitAll()

                        // ADMIN
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        // DIRECTOR
                        .requestMatchers("/api/director/**").hasRole("DIRECTOR")

                        // DEPARTMENTS
                        .requestMatchers(HttpMethod.GET, "/api/departments/**")
                        .hasAnyRole("DIRECTOR", "TEACHER")
                        .requestMatchers(HttpMethod.POST, "/api/departments/**")
                        .hasRole("DIRECTOR")
                        .requestMatchers(HttpMethod.PUT, "/api/departments/**")
                        .hasRole("DIRECTOR")
                        .requestMatchers(HttpMethod.DELETE, "/api/departments/**")
                        .hasRole("DIRECTOR")

                        // CLASSES
                        .requestMatchers("/api/classes/**")
                        .hasAnyRole("DIRECTOR", "ADMIN", "TEACHER")
                        .requestMatchers(HttpMethod.GET,"/api/classes/teacher/**")
                        .hasAnyRole("TEACHER", "DIRECTOR", "ADMIN")

                        // REGISTRATIONS
                        .requestMatchers("/api/registrations/**")
                        .hasAnyRole("DIRECTOR", "ADMIN", "TEACHER", "STUDENT")

                        // COURSES
                        .requestMatchers(HttpMethod.GET, "/api/courses/**")
                        .hasAnyRole("STUDENT", "TEACHER", "DIRECTOR", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/courses/**")
                        .hasAnyRole("DIRECTOR", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/courses/**")
                        .hasAnyRole("DIRECTOR", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/courses/**")
                        .hasAnyRole("DIRECTOR", "ADMIN")

                        // GRADES
                        .requestMatchers(HttpMethod.GET, "/api/grades/**")
                        .hasAnyRole("STUDENT", "TEACHER", "DIRECTOR", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/grades/**")
                        .hasRole("TEACHER")
                        .requestMatchers(HttpMethod.PUT, "/api/grades/**")
                        .hasRole("TEACHER")
                        .requestMatchers(HttpMethod.DELETE, "/api/grades/**")
                        .hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/grades/filter/teacher")
                        .hasAnyRole("DIRECTOR", "TEACHER")

                        // FREQUENCIES
                        .requestMatchers("/api/frequencies/**")
                        .hasAnyRole("STUDENT", "TEACHER", "DIRECTOR", "ADMIN")

                        // AVERAGES / TRANSCRIPTS
                        .requestMatchers("/api/averages/**")
                        .hasAnyRole("STUDENT", "TEACHER", "DIRECTOR", "ADMIN")
                        .requestMatchers("/api/transcripts/**")
                        .hasAnyRole("STUDENT", "DIRECTOR", "ADMIN")

                        // TEACHER CLASS
                        .requestMatchers(HttpMethod.POST, "/api/teacher-classes")
                        .hasAnyRole("DIRECTOR", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/teacher-classes")
                        .hasAnyRole("DIRECTOR", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/teacher-classes/{id}") // ADICIONADO
                        .hasAnyRole("DIRECTOR", "ADMIN", "TEACHER")
                        .requestMatchers(HttpMethod.GET, "/api/teacher-classes/class/**")
                        .hasAnyRole("DIRECTOR", "ADMIN", "TEACHER") // ADICIONEI TEACHER AQUI
                        .requestMatchers(HttpMethod.GET, "/api/teacher-classes/teacher/**")
                        .hasAnyRole("TEACHER", "DIRECTOR", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/teacher-classes/teacher/*/course/*") // ADICIONADO
                        .hasAnyRole("TEACHER", "DIRECTOR", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/teacher-classes/**") // ADICIONADO
                        .hasAnyRole("DIRECTOR", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/teacher-classes/**")
                        .hasAnyRole("DIRECTOR", "ADMIN")

                        // DISCIPLINES
                        .requestMatchers(HttpMethod.POST, "/api/disciplines")
                        .hasAnyRole("ADMIN", "DIRECTOR")
                        .requestMatchers(HttpMethod.GET, "/api/disciplines") // ADICIONADO
                        .hasAnyRole("STUDENT", "DIRECTOR", "ADMIN", "TEACHER")
                        .requestMatchers(HttpMethod.GET, "/api/disciplines/{id}")
                        .hasAnyRole("STUDENT", "DIRECTOR", "ADMIN", "TEACHER")
                        .requestMatchers(HttpMethod.GET, "/api/disciplines/code/**") // ADICIONADO
                        .hasAnyRole("STUDENT", "DIRECTOR", "ADMIN", "TEACHER")
                        .requestMatchers(HttpMethod.GET, "/api/disciplines/course/**")
                        .hasAnyRole("DIRECTOR", "ADMIN", "TEACHER") // ADICIONEI TEACHER AQUI
                        .requestMatchers(HttpMethod.GET, "/api/disciplines/teacher/**") // ADICIONADO
                        .hasAnyRole("TEACHER", "DIRECTOR", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/disciplines/page") // ADICIONADO
                        .hasAnyRole("DIRECTOR", "ADMIN", "TEACHER")
                        .requestMatchers(HttpMethod.PUT, "/api/disciplines/**")
                        .hasAnyRole("DIRECTOR", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/disciplines/**")
                        .hasAnyRole("DIRECTOR", "ADMIN")

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
