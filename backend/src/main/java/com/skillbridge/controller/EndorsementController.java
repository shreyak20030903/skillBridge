package com.skillbridge.controller;

import com.skillbridge.dto.LeaderboardEntryDto;
import com.skillbridge.model.EndorsedRelationship;
import com.skillbridge.model.Person;
import com.skillbridge.repository.PersonRepository;
import com.skillbridge.service.GraphQueryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class EndorsementController {

    private final PersonRepository personRepository;
    private final GraphQueryService graphQueryService;

    public EndorsementController(PersonRepository personRepository, GraphQueryService graphQueryService) {
        this.personRepository = personRepository;
        this.graphQueryService = graphQueryService;
    }

    @GetMapping("/people/{personId}/endorsements")
    public ResponseEntity<List<EndorsedRelationship>> getEndorsements(@PathVariable String personId) {
        return personRepository.findById(personId)
                .map(p -> ResponseEntity.ok(p.getEndorsementsReceived()))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    public record EndorsementRequest(String endorserId, String skillName, Integer weight, String note) {}

    @PostMapping("/people/{personId}/endorsements")
    public ResponseEntity<?> addEndorsement(@PathVariable String personId,
                                            @RequestBody EndorsementRequest request) {
        Person endorsee = personRepository.findById(personId).orElse(null);
        Person endorser = personRepository.findById(request.endorserId()).orElse(null);
        if (endorsee == null || endorser == null) {
            return ResponseEntity.notFound().build();
        }
        endorsee.getEndorsementsReceived().add(new EndorsedRelationship(
                endorser, request.skillName(), request.weight() == null ? 1 : request.weight(), request.note()));
        personRepository.save(endorsee);
        return ResponseEntity.ok().build();
    }

    /** GET /api/endorsements/leaderboard -> top 10 most-endorsed people overall */
    @GetMapping("/endorsements/leaderboard")
    public List<LeaderboardEntryDto> getLeaderboard() {
        return graphQueryService.findEndorsementLeaderboard();
    }
}