package com.skillbridge.controller;

import com.skillbridge.dto.ConnectionPathDto;
import com.skillbridge.service.GraphQueryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/connections")
public class ConnectionController {

    private final GraphQueryService graphQueryService;

    public ConnectionController(GraphQueryService graphQueryService) {
        this.graphQueryService = graphQueryService;
    }

    /** GET /api/connections/path?fromId=...&toId=...&via=any|work|skills|endorsements */
    @GetMapping("/path")
    public ResponseEntity<ConnectionPathDto> shortestPath(@RequestParam String fromId,
                                                          @RequestParam String toId,
                                                          @RequestParam(defaultValue = "any") String via) {
        ConnectionPathDto path = graphQueryService.findShortestConnection(fromId, toId, via);
        if (path == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(path);
    }
}