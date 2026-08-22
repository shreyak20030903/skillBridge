package com.skillbridge.controller;

import com.skillbridge.dto.MentorSuggestionDto;
import com.skillbridge.service.GraphQueryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/mentors")
public class MentorController {

    private final GraphQueryService graphQueryService;

    public MentorController(GraphQueryService graphQueryService) {
        this.graphQueryService = graphQueryService;
    }

    /**
     * GET /api/mentors?requesterId=...&skill=AWS
     * Finds potential mentors for `skill` who share a project with the requester
     * (2-hop traversal: requester -> project -> mentor -> skill).
     */
    @GetMapping
    public List<MentorSuggestionDto> findMentors(@RequestParam String requesterId,
                                                  @RequestParam String skill) {
        return graphQueryService.findMentorsViaSharedProject(requesterId, skill);
    }
}
