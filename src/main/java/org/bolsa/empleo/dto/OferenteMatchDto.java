package org.bolsa.empleo.dto;

public class OferenteMatchDto {
    private Integer idOferente;
    private String nombre;
    private Integer scoreCoincidencia;
    private Integer totalRequeridas;

    public OferenteMatchDto(Integer idOferente, String nombre,
                            Integer scoreCoincidencia, Integer totalRequeridas) {
        this.idOferente = idOferente;
        this.nombre = nombre;
        this.scoreCoincidencia = scoreCoincidencia;
        this.totalRequeridas = totalRequeridas;
    }

    public Integer getIdOferente() { return idOferente; }
    public String getNombre() { return nombre; }
    public Integer getScoreCoincidencia() { return scoreCoincidencia; }
    public Integer getTotalRequeridas() { return totalRequeridas; }


    public double getPorcentajeCoincidencia() {
        if (totalRequeridas == null || totalRequeridas == 0) return 0.0;
        return (scoreCoincidencia * 100.0) / totalRequeridas;
    }
}

