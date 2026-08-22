package com.skillbridge.controller;

import com.skillbridge.dto.ProjectContributorDto;
import com.skillbridge.dto.ProjectSummaryDto;
import com.skillbridge.service.GraphQueryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final GraphQueryService graphQueryService;

    public ProjectController(GraphQueryService graphQueryService) {
        this.graphQueryService = graphQueryService;
    }

    /** GET /api/projects -> all projects with contributor counts */
    @GetMapping
    public List<ProjectSummaryDto> listProjects() {
        return graphQueryService.findProjectsWithContributorCounts();
    }

    /** GET /api/projects/{id}/contributors -> everyone who worked on this project */
    @GetMapping("/{id}/contributors")
    public List<ProjectContributorDto> getContributors(@PathVariable String id) {
        return graphQueryService.findContributorsForProject(id);
    }
}