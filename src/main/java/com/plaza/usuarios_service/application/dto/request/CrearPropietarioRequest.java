package com.plaza.usuarios_service.application.dto.request;


import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.time.Period;

public class CrearPropietarioRequest {

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    private String apellido;

    @Pattern(regexp = "^[0-9]+$", message = "El documento debe ser numérico")
    @NotBlank(message = "El documento es obligatorio")
    private String documentoIdentidad;

    @Size(max = 13, message = "El celular no puede superar 13 caracteres")
    @Pattern(regexp = "^\\+?[0-9]+$", message = "El celular solo puede contener números y opcionalmente + al inicio")
    private String celular;

    @Past(message = "La fecha de nacimiento debe ser en el pasado")
    private LocalDate fechaNacimiento;

    @Email(message = "Correo inválido")
    @NotBlank(message = "El correo es obligatorio")
    private String correo;

    @NotBlank(message = "La clave es obligatoria")
    private String clave;

    public CrearPropietarioRequest() {}

    // Validación de mayor de edad
    public boolean esMayorDeEdad() {
        return fechaNacimiento != null &&
                Period.between(fechaNacimiento, LocalDate.now()).getYears() >= 18;
    }

    // Getters y Setters
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getDocumentoIdentidad() { return documentoIdentidad; }
    public void setDocumentoIdentidad(String documentoIdentidad) { this.documentoIdentidad = documentoIdentidad; }

    public String getCelular() { return celular; }
    public void setCelular(String celular) { this.celular = celular; }

    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(LocalDate fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getClave() { return clave; }
    public void setClave(String clave) { this.clave = clave; }
}
