package com.plaza.usuarios_service.application.dto.request;

import jakarta.validation.constraints.*;

public class CrearClienteRequest {

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    private String apellido;

    @NotBlank(message = "El documento es obligatorio")
    @Pattern(regexp = "^[0-9]+$", message = "El documento debe ser numérico")
    private String documentoIdentidad;

    @NotBlank(message = "El celular es obligatorio")
    @Size(max = 13, message = "El celular no puede superar 13 caracteres")
    @Pattern(
            regexp = "^\\+?[0-9]+$",
            message = "El celular solo puede contener números y opcionalmente + al inicio"
    )
    private String celular;

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "Correo inválido")
    private String correo;

    @NotBlank(message = "La clave es obligatoria")
    @Size(min = 8, message = "La clave debe tener mínimo 8 caracteres")
    private String clave;

    public CrearClienteRequest() {}

    // Getters y Setters
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getDocumentoIdentidad() { return documentoIdentidad; }
    public void setDocumentoIdentidad(String documentoIdentidad) {
        this.documentoIdentidad = documentoIdentidad;
    }

    public String getCelular() { return celular; }
    public void setCelular(String celular) { this.celular = celular; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getClave() { return clave; }
    public void setClave(String clave) { this.clave = clave; }
}
