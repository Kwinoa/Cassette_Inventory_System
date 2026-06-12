package org.cassettes.cassetteinventorysystem.controller;

import java.util.Map;

import org.cassettes.cassetteinventorysystem.entity.ResponseStructure;
import org.cassettes.cassetteinventorysystem.service.CassetteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StatsController {

    @Autowired
    private CassetteService cassetteService;

    @GetMapping("/dashboard")
    public ResponseEntity<ResponseStructure<Map<String, Object>>> getCollectionStats() {
        // We calculate these in the service layer
        Map<String, Object> stats = cassetteService.getStatsForUser();
        
        ResponseStructure<Map<String, Object>> structure = new ResponseStructure<>();
        structure.setData(stats);
        structure.setMessage("Stats retrieved successfully");
        structure.setStatusCode(HttpStatus.OK.value());
        
        return new ResponseEntity<>(structure, HttpStatus.OK);
    }
}
	
