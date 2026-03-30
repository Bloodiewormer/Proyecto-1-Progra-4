package org.bolsa.empleo.dto;

import jakarta.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.bolsa.empleo.model.TipoPublicacion;

import java.math.BigDecimal;

public class PuestoCreateDto {
    @NotBlank
    private String titulo;

    @NotBlank
    private String descripcion;

    @NotNull
    @DecimalMin(value = "0.01")
    private BigDecimal salario;

    @NotBlank(message = "El tipo de publicación es obligatorio (PUBLICO o PRIVADO)")
    private String tipoPublicacion;

    @NotNull(message = "Debe seleccionar al menos una característica requerida")
    private List<CaracteristicaNivelDto> caracteristicasRequeridas;

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public BigDecimal getSalario() {
        return salario;
    }

    public void setSalario(BigDecimal salario) {
        this.salario = salario;
    }

    public String getTipoPublicacion() { return tipoPublicacion; }
    public void setTipoPublicacion(String tipoPublicacion) { this.tipoPublicacion = tipoPublicacion; }

    public List<CaracteristicaNivelDto> getCaracteristicasRequeridas() { return caracteristicasRequeridas; }
    public void setCaracteristicasRequeridas(List<CaracteristicaNivelDto> caracteristicasRequeridas) {
        this.caracteristicasRequeridas = caracteristicasRequeridas;
    }
}

