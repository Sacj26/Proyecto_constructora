package com.example.constructora.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.constructora.model.Persona;
import com.example.constructora.repository.PersonaRepository;

@Service
public class PersonaService {
    
    @Autowired
    private PersonaRepository personaRepository;

    public void guardarPersona(Persona persona) {
        personaRepository.save(persona);
    }

    public Persona obtenerPersonaPorId(String id) {
        return personaRepository.findById(id).orElse(null);
    }

    public List<Persona> listarPersonas() {
        return personaRepository.findAll();
    }
}
