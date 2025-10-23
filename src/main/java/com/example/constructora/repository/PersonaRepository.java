package com.example.constructora.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import com.example.constructora.model.Persona;

@Repository
public interface PersonaRepository extends MongoRepository<Persona, String> {

}
