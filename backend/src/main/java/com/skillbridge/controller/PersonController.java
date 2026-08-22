package com.skillbridge.controller;

import com.skillbridge.model.Person;
import com.skillbridge.repository.PersonRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/people")
public class PersonController {

    private final PersonRepository personRepository;

    public PersonController(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    /** GET /api/people?skill=Spring+Boot  -> people who have that skill */
    @GetMapping
    public List<Person> search(@RequestParam(required = false) String skill,
                                @RequestParam(required = false) String name) {
        if (skill != null && !skill.isBlank()) {
            return personRepository.findPeopleWithSkill(skill);
        }
        if (name != null && !name.isBlank()) {
            return personRepository.findByNameContainingIgnoreCase(name);
        }
        return personRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Person> getById(@PathVariable String id) {
        return personRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
