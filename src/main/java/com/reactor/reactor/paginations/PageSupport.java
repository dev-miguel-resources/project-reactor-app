package com.reactor.reactor.paginations;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageSupport<T> {

    public static final String FIRST_PAGE_NUM = "0"; // referencia de la página 1 para ocuparla en el método first
    public static final String DEFAULT_PAGE_SIZE = "10"; // valor de referencia para el ej.

    private List<T> content; // Ej: ["Pizza", "Sushi", "Tacos"]
    private int pageNumber; // Ej: 0 (primera página)
    private int pageSize; // Ej: 3 (elementos por página)

    private long totalElements; // Ej: 7 (total de elementos en todas las páginas)

    // te permite incorporar métodos como resultado de un JSON
    @JsonProperty
    public long totalPages() {
        // Si pageSize = 3 y totalElements = 7
        // totalPages = (7 - 1) / 3 + 1 = 6/3 + 1 = 2 + 1 = 3 páginas en total
        return pageSize > 0 ? (totalElements - 1) / pageSize + 1 : 0;
    }

    @JsonProperty
    public boolean first() {
        // Si pageNumber = 0 (primera página), devuelve true
        // Si pageNumber fuera = 1 o más, devuelve false
        return pageNumber == Integer.parseInt(FIRST_PAGE_NUM);
    }

    @JsonProperty
    public boolean last() {
        // Si pageNumber = 2, pageSize = 3 y totalElements = 7;
        // (2 + 1) * 3 >= 7 -> true (a que es la última página)
        // Si pageNumber fuera 1
        // (1 + 1) * 3 = 6 >= 7 -> false (no es la última página)
        return (pageNumber + 1) * pageSize >= totalElements;
    }

}
