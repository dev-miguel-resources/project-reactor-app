package com.reactor.reactor.models;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Document(collection = "menus")
public class Menu {

    @Id
    @EqualsAndHashCode.Include
    private String id;

    @Field
    private String icon;

    @Field
    private String name;

    @Field
    private String url;

    @Field
    private List<String> roles;

}
