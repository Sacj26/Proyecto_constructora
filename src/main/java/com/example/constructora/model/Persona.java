package com.example.constructora.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "personas")

public class Persona {
    @Id
    private String id;
    private String nombres;
    private String apellidos;
    private String tipoId;             // camelCase
    private int numeroId;               // camelCase
    private String genero;
    private int edad;
    private int disposicionSalarial;   // camelCase
    private String rol;
    private boolean jefe = false;
}
