package org.cassettes.cassetteinventorysystem.controller;

import java.util.List;

import org.cassettes.cassetteinventorysystem.entity.Cassette;
import org.cassettes.cassetteinventorysystem.entity.ResponseStructure;
import org.cassettes.cassetteinventorysystem.service.CassetteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
public class CassetteController {
	
	@Autowired
	private CassetteService cassetteService;

	@PostMapping(value = "/saveCassette")
	public ResponseEntity<ResponseStructure<Cassette>> addCassettes(@RequestBody Cassette cassette) {
		return cassetteService.addCassettes(cassette);
	}
	
	@GetMapping(value = "/getAllCassettes")
	public ResponseEntity<ResponseStructure<List<Cassette>>> getAllCassettes(){
		return cassetteService.getAllCassettes();
	}
	
	@GetMapping(value = "/cassette/{id}")
	public ResponseEntity<ResponseStructure<Cassette>> getCassetteById(@PathVariable int id){
		return cassetteService.getCassetteById(id);
	}
	
	@PutMapping(value = "/cassette/{id}")
	public ResponseEntity<ResponseStructure<Cassette>> updateCassetteById(@RequestBody Cassette cassette, @PathVariable int id){
		return cassetteService.updateCassetteById(cassette, id);
	}
	
	@DeleteMapping(value = "/delete/{id}")
	public ResponseEntity<ResponseStructure<String>> deleteCassetteById(@PathVariable int id){
		return cassetteService.deleteCassetteById(id);
	}
}
