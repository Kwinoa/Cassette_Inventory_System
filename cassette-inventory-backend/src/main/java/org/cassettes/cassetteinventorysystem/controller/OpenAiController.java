package org.cassettes.cassetteinventorysystem.controller;

import java.util.List;

import org.cassettes.cassetteinventorysystem.entity.Cassette;
import org.cassettes.cassetteinventorysystem.entity.ResponseStructure;
import org.cassettes.cassetteinventorysystem.service.DiscogsService;
import org.cassettes.cassetteinventorysystem.service.OpenAiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "https://localhost:3000")
public class OpenAiController {
	
	@Autowired
	OpenAiService openAiService;
	@Autowired
	DiscogsService discogsService;
	
	public OpenAiController(OpenAiService openAiService, DiscogsService discogsService) {
		this.openAiService = openAiService;
		this.discogsService = discogsService;
	}

	@GetMapping(value = "/smartSearch")
	@CrossOrigin(origins = "https://localhost:3000")
	public ResponseEntity<ResponseStructure<List<Cassette>>> getAllCassettes(){
		ResponseStructure<List<Cassette>> structure = new ResponseStructure<>();
		List<Cassette> recommendedCassettes = openAiService.getCassetteRecommendations();
		
		structure.setData(recommendedCassettes);
		structure.setMessage("Cassette Query Successful");
		structure.setStatusCode(HttpStatus.ACCEPTED.value());
		return new ResponseEntity<ResponseStructure<List<Cassette>>>(structure, HttpStatus.ACCEPTED);
	}
}
