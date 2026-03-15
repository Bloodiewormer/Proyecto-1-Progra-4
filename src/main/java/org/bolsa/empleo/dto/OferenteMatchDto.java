package org.bolsa.empleo.dto;

public class OferenteMatchDto {
    private Integer idOferente;
    private String nombre;
    private Integer scoreCoincidencia;

    public OferenteMatchDto(Integer idOferente, String nombre, Integer scoreCoincidencia) {
        this.idOferente = idOferente;
        this.nombre = nombre;
        this.scoreCoincidencia = scoreCoincidencia;
    }

    public Integer getIdOferente() {
        return idOferente;
    }

    public String getNombre() {
        return nombre;
    }

    public Integer getScoreCoincidencia() {
        return scoreCoincidencia;
    }
}

