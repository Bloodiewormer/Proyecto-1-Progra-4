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
    private Puesto puesto;

    @MapsId("idCaracteristica")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_caracteristica", nullable = false)
    private Caracteristica caracteristica;

    @NotNull
    @Column(name = "nivel_requerido", nullable = false)
    private Integer nivelRequerido;

    public PuestoCaracteristicaId getId() {
        return id;
    }

    public void setId(PuestoCaracteristicaId id) {
        this.id = id;
    }

    public Puesto getPuesto() {
        return puesto;
    }

    public void setPuesto(Puesto puesto) {
        this.puesto = puesto;
    }

    public Caracteristica getCaracteristica() {
        return caracteristica;
    }

    public void setCaracteristica(Caracteristica caracteristica) {
        this.caracteristica = caracteristica;
    }

    public Integer getNivelRequerido() {
        return nivelRequerido;
    }

    public void setNivelRequerido(Integer nivelRequerido) {
        this.nivelRequerido = nivelRequerido;
    }

}