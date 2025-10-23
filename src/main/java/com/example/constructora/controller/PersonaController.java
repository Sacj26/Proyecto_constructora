package com.example.constructora.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.constructora.model.Persona;
import com.example.constructora.repository.PersonaRepository;

@Controller
public class PersonaController {

    @Autowired
    private PersonaRepository personaRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @GetMapping("/registro")
    public String mostrarFormulario(Model model, @RequestParam(required = false) String exito) {
        model.addAttribute("persona", new Persona());
        model.addAttribute("personas", personaRepository.findAll());

        if (exito != null) {
            model.addAttribute("mensaje", "Registro exitoso");
        }
        return "registro";
    }

    @PostMapping("/registro")
    public String registrarPersona(@ModelAttribute Persona persona) {
        personaRepository.save(persona);
        return "redirect:/registro?exito";
    }

    @GetMapping("/consultas")
    public String mostrarConsultas(Model model) {
        model.addAttribute("personas", personaRepository.findAll());
        return "consultas";
    }

    @GetMapping("/jefe")
    public String mostrarJefe(Model model) {
        model.addAttribute("personas", personaRepository.findAll());
        return "jefe";
    }

    @GetMapping("/inicio")
    public String mostrarInicio() {
        return "inicio";
    }

    @PostMapping("/determinar-jefe")
    public String determinarJefe(Model model) {

        // ✅ Criterios
        int edadMinima = 25;
        int salarioMinimo = 3000000;
        List<String> rolesPermitidos = List.of("master", "especialista", "operador", "almacen");

        // 1️⃣ Filtrar personas
        Query filtro = new Query();
        filtro.addCriteria(
                Criteria.where("edad").gt(edadMinima)
                        .and("disposicionSalarial").gt(salarioMinimo)
                        .and("rol").in(rolesPermitidos)
        );
        List<Persona> personasFiltradas = mongoTemplate.find(filtro, Persona.class);

        if (personasFiltradas.isEmpty()) {
            model.addAttribute("mensaje", "No se encontraron personas que cumplan con los criterios.");
            return "jefe";
        }

        // 2️⃣ Calcular promedio
        double promedio = personasFiltradas.stream()
                .mapToInt(Persona::getDisposicionSalarial)
                .average()
                .orElse(0);

        // 3️⃣ Encontrar jefe
        Persona jefe = personasFiltradas.stream()
                .filter(p -> p.getDisposicionSalarial() > promedio)
                .max((p1, p2) -> Integer.compare(p1.getDisposicionSalarial(), p2.getDisposicionSalarial()))
                .orElse(null);

        // 4️⃣ Limpiar jefe anterior
        mongoTemplate.updateMulti(new Query(), Update.update("jefe", false), Persona.class);

        if (jefe != null) {
            jefe.setJefe(true);
            mongoTemplate.save(jefe);
            model.addAttribute("mensaje", "✅ El jefe seleccionado es: " + jefe.getNombres() + " " + jefe.getApellidos());
            model.addAttribute("jefe", jefe); // enviar datos del jefe
        }

        // 5️⃣ Enviar datos para mostrar filtros y personas filtradas
        model.addAttribute("promedio", promedio);
        model.addAttribute("filtros", "Edad > " + edadMinima + ", Salario > " + salarioMinimo + ", Roles: " + rolesPermitidos);
        model.addAttribute("personasFiltradas", personasFiltradas);
        model.addAttribute("personas", mongoTemplate.findAll(Persona.class)); // tabla completa
        return "jefe";
    }

    //Hola
}
