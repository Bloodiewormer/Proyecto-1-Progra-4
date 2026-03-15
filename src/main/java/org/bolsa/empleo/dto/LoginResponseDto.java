package org.bolsa.empleo.dto;

public class LoginResponseDto {
    private Integer idUsuario;
    private String rol;
    private String estado;

    public LoginResponseDto(Integer idUsuario, String rol, String estado) {
        this.idUsuario = idUsuario;
        this.rol = rol;
        this.estado = estado;
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public String getRol() {
        return rol;
    }

    public String getEstado() {
        return estado;
    }
}

