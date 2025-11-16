package com.studium.studium_academico.infrastructure.entity;

public enum UserRole {
    ADMIN("admin"),
    DIRECTOR("director"),
    TEACHER("teacher"),
    STUDENT("student");

    private String role;
    UserRole(String role) {
        this.role = role;
    }
    public String getRole() {
        return role;
    }
}
