package org.cassettes.cassetteinventorysystem.controller;

import java.util.List;

import org.cassettes.cassetteinventorysystem.entity.Cassette;
import org.cassettes.cassetteinventorysystem.entity.ResponseStructure;
//import org.cassettes.cassetteinventorysystem.entity.CassetteAPI;
import org.cassettes.cassetteinventorysystem.service.DiscogsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
//@RequestMapping("/search")
public class DiscogsController {

	@Autowired	
	private DiscogsService discogsService;
	
	public DiscogsController(DiscogsService discogsService) {
		this.discogsService = discogsService;
	}
	
	@GetMapping("/search")
	public ResponseEntity<ResponseStructure<List<Cassette>>> searchSongs(@RequestParam String query){
		ResponseStructure<List<Cassette>> structure = new ResponseStructure<>();
		String response = discogsService.searchAlbum(query, 80);
		List<Cassette> cassette = discogsService.extractCassetteInfo(response);		
		System.out.println("\n\n==============================");
		System.out.println("Searching songs...");

		structure.setData(cassette);
		structure.setMessage("Cassette Query Successful");
		structure.setStatusCode(HttpStatus.ACCEPTED.value());
		return new ResponseEntity<ResponseStructure<List<Cassette>>>(structure, HttpStatus.ACCEPTED);
	}
	
	@GetMapping("/search/album")
	public ResponseEntity<ResponseStructure<Cassette>> searchAlbum(@RequestParam String query) {
		ResponseStructure<Cassette> structure = new ResponseStructure<>();
		String response = discogsService.searchAlbum(query, 1);
		Cassette cassette = null;
		List<Cassette> cassettes = discogsService.extractCassetteInfo(response);
		if(cassettes != null && !cassettes.isEmpty()) {
			cassette = cassettes.get(0);
		}
		
		structure.setData(cassette);
		structure.setMessage("Cassette Query Successful");
		structure.setStatusCode(HttpStatus.ACCEPTED.value());
		
		return new ResponseEntity<ResponseStructure<Cassette>>(structure, HttpStatus.ACCEPTED);
	}
	
	@GetMapping("/search/master")
	public String searchMaster(@RequestParam String query) {
		String response = discogsService.searchMaster(query);
		return response;
	}
}
