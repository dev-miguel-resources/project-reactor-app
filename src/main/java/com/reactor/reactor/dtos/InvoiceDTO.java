package com.reactor.reactor.dtos;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InvoiceDTO {

    private String id;
    private String description;
    private ClientDTO client;
    private List<InvoiceDetailDTO> items;

}
