package org.bolsa.empleo.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class PuestoFiltroDto {

    private String palabraClave;
    private BigDecimal salarioMin;
    private BigDecimal salarioMax;

    // IDs de características seleccionadas en la búsqueda pública
    private List<Integer> idsCaracteristicas = new ArrayList<>();

    public String getPalabraClave() { return palabraClave; }
    public void setPalabraClave(String palabraClave) { this.palabraClave = palabraClave; }

    public BigDecimal getSalarioMin() { return salarioMin; }
    public void setSalarioMin(BigDecimal salarioMin) { this.salarioMin = salarioMin; }

    public BigDecimal getSalarioMax() { return salarioMax; }
    public void setSalarioMax(BigDecimal salarioMax) { this.salarioMax = salarioMax; }

    public List<Integer> getIdsCaracteristicas() { return idsCaracteristicas; }
    public void setIdsCaracteristicas(List<Integer> idsCaracteristicas) {
        this.idsCaracteristicas = idsCaracteristicas != null ? idsCaracteristicas : new ArrayList<>();
    }
}