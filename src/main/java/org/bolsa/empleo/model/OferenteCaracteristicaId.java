package org.bolsa.empleo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class OferenteCaracteristicaId implements Serializable {
    private static final long serialVersionUID = -2407781767790851313L;
    @NotNull
    @Column(name = "id_oferente", nullable = false)
    private Integer idOferente;

    @NotNull
    @Column(name = "id_caracteristica", nullable = false)
    private Integer idCaracteristica;

    public Integer getIdOferente() {
        return idOferente;
    }

    public void setIdOferente(Integer idOferente) {
        this.idOferente = idOferente;
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
        OferenteCaracteristicaId entity = (OferenteCaracteristicaId) o;
        return Objects.equals(this.idCaracteristica, entity.idCaracteristica) &&
                Objects.equals(this.idOferente, entity.idOferente);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idCaracteristica, idOferente);
    }

}