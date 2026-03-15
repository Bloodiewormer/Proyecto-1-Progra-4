package org.bolsa.empleo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "puesto_caracteristica")
public class PuestoCaracteristica {
    @EmbeddedId
    private PuestoCaracteristicaId id;

    @MapsId("idPuesto")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "id_puesto", nullable = false)
    private Puesto idPuesto;

    @MapsId("idCaracteristica")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_caracteristica", nullable = false)
    private Caracteristica idCaracteristica;

    @NotNull
    @Column(name = "nivel_requerido", nullable = false)
    private Integer nivelRequerido;

    public PuestoCaracteristicaId getId() {
        return id;
    }

    public void setId(PuestoCaracteristicaId id) {
        this.id = id;
    }

    public Puesto getIdPuesto() {
        return idPuesto;
    }

    public void setIdPuesto(Puesto idPuesto) {
        this.idPuesto = idPuesto;
    }

    public Caracteristica getIdCaracteristica() {
        return idCaracteristica;
    }

    public void setIdCaracteristica(Caracteristica idCaracteristica) {
        this.idCaracteristica = idCaracteristica;
    }

    public Integer getNivelRequerido() {
        return nivelRequerido;
    }

    public void setNivelRequerido(Integer nivelRequerido) {
        this.nivelRequerido = nivelRequerido;
    }

}