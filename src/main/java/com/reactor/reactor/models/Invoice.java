package com.reactor.reactor.models;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Document(collection = "invoices")
public class Invoice {

    @Id
    @EqualsAndHashCode.Include
    private String id;

    @Field
    private String description;

    @Field
    private Client client;

    private List<InvoiceDetail> items;

}
