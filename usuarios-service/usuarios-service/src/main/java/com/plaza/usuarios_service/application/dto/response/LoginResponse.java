package com.plaza.usuarios_service.application.dto.response;

public class LoginResponse {

    private String nombre;
    private String rol;
    private String token;

    public LoginResponse() {}

    public LoginResponse(String nombre, String rol, String token) {
        this.nombre = nombre;
        this.rol = rol;
        this.token = token;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "LoginResponse{" +
                "nombre='" + nombre + '\'' +
                ", rol='" + rol + '\'' +
                ", token='" + token + '\'' +
                '}';
    }
}
