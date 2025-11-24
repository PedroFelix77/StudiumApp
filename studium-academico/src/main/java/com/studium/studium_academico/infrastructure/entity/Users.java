package com.studium.studium_academico.infrastructure.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Users extends BaseEntity implements UserDetails {

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "cpf", nullable = false, unique = true)
    private String cpf;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password",  nullable = false)
    private String password;

    @Column(name = "birthday")
    private LocalDate birthday;

    @Column(name = "phone",  nullable = false)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    @ManyToOne
    @JoinColumn(name = "institution_id",  nullable = false)
    private Institution institution;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch =  FetchType.LAZY)
    private Student student;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch =  FetchType.LAZY)
    private Teacher teacher;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL,  fetch =  FetchType.LAZY)
    private Admin admin;

    @ManyToOne
    @JoinColumn(name = "address_id", nullable = false)
    private Address address;

    public Users(String name, String cpf, String email, String password, LocalDate birthday, String phone, UserRole role) {
        this.name = name;
        this.cpf = cpf;
        this.email = email;
        this.password = password;
        this.birthday = birthday;
        this.phone = phone;
        this.role = role;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (this.role == UserRole.ADMIN) {
            return List.of(
                    new SimpleGrantedAuthority("ROLE_ADMIN"),
                    new SimpleGrantedAuthority("ROLE_DIRECTOR"),
                    new SimpleGrantedAuthority("ROLE_TEACHER"),
                    new SimpleGrantedAuthority("ROLE_STUDENT")
            );
        } else if (this.role == UserRole.DIRECTOR) {
            return List.of(
                    new SimpleGrantedAuthority("ROLE_DIRECTOR"),
                    new SimpleGrantedAuthority("ROLE_TEACHER"),
                    new SimpleGrantedAuthority("ROLE_STUDENT")
            );
        } else if (this.role == UserRole.TEACHER) {
            return List.of(
                    new SimpleGrantedAuthority("ROLE_TEACHER"),
                    new SimpleGrantedAuthority("ROLE_STUDENT")
            );
        } else {
            // STUDENT
            return List.of(
                    new SimpleGrantedAuthority("ROLE_STUDENT")
            );
        }
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
       return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
