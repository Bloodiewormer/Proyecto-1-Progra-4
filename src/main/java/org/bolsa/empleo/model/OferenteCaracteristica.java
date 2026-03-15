package org.bolsa.empleo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "oferente_caracteristica")
public class OferenteCaracteristica {
    @EmbeddedId
    private OferenteCaracteristicaId id;

    @MapsId("idOferente")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "id_oferente", nullable = false)
    private Oferente idOferente;

    @MapsId("idCaracteristica")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_caracteristica", nullable = false)
    private Caracteristica idCaracteristica;

    @NotNull
    @Column(name = "nivel", nullable = false)
    private Integer nivel;

    public OferenteCaracteristicaId getId() {
        return id;
    }

    public void setId(OferenteCaracteristicaId id) {
        this.id = id;
    }

    public Oferente getIdOferente() {
        return idOferente;
    }

    public void setIdOferente(Oferente idOferente) {
        this.idOferente = idOferente;
    }

    public Caracteristica getIdCaracteristica() {
        return idCaracteristica;
    }

    public void setIdCaracteristica(Caracteristica idCaracteristica) {
        this.idCaracteristica = idCaracteristica;
    }

    public Integer getNivel() {
        return nivel;
    }

    public void setNivel(Integer nivel) {
        this.nivel = nivel;
    }

}