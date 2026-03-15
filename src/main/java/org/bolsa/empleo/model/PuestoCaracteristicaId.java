package org.bolsa.empleo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class PuestoCaracteristicaId implements Serializable {
    private static final long serialVersionUID = 2510082469133067866L;
    @NotNull
    @Column(name = "id_puesto", nullable = false)
    private Integer idPuesto;

    @NotNull
    @Column(name = "id_caracteristica", nullable = false)
    private Integer idCaracteristica;

    public Integer getIdPuesto() {
        return idPuesto;
    }

    public void setIdPuesto(Integer idPuesto) {
        this.idPuesto = idPuesto;
    }

    public Integer getIdCaracteristica() {
        return idCaracteristica;
    }

    public void setIdCaracteristica(Integer idCaracteristica) {
        this.idCaracteristica = idCaracteristica;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        PuestoCaracteristicaId entity = (PuestoCaracteristicaId) o;
        return Objects.equals(this.idPuesto, entity.idPuesto) &&
                Objects.equals(this.idCaracteristica, entity.idCaracteristica);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idPuesto, idCaracteristica);
    }

}