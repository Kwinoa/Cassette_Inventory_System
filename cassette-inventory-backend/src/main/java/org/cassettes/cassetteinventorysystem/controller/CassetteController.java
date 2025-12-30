package org.cassettes.cassetteinventorysystem.controller;

import java.util.List;

import org.cassettes.cassetteinventorysystem.entity.Cassette;
import org.cassettes.cassetteinventorysystem.entity.ResponseStructure;
import org.cassettes.cassetteinventorysystem.entity.User;
import org.cassettes.cassetteinventorysystem.service.CassetteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;

@RestController
public class CassetteController {
	@Autowired
	private CassetteService cassetteService;

	// Retrieves a JSON param that is automatically deserialized into a cassette
	@PostMapping(value = "/saveCassette")
	public ResponseEntity<ResponseStructure<Cassette>> addCassettes(@RequestBody Cassette cassette) {
        
		Cassette data = cassetteService.addCassettes(cassette);
		
		ResponseStructure<Cassette> structure = new ResponseStructure<>();

		structure.setData(data);
		structure.setMessage("Cassette Added Sucessfully");
		structure.setStatusCode(HttpStatus.CREATED.value());
		
		return new ResponseEntity<ResponseStructure<Cassette>>(structure, HttpStatus.CREATED);
	}
	
	@PostMapping(value = "/uploadImage", consumes = "multipart/form-data")
    public ResponseEntity<ResponseStructure<String>> uploadCoverImage(
            @RequestParam("file") MultipartFile file) {
        return cassetteService.uploadCoverImage(file);
    }
	
//	@GetMapping(value = "/getAllCassettes")
//	public ResponseEntity<ResponseStructure<List<Cassette>>> getAllCassettes(){
//		return cassetteService.getAllCassettes();
//	}
	
	@GetMapping(value = "/getUserCassettes")
	public ResponseEntity<ResponseStructure<List<Cassette>>> getUserCassettes(){
		return cassetteService.getUserCassettes();
	}
	
	@GetMapping(value = "/cassette/{id}")
	public ResponseEntity<ResponseStructure<Cassette>> getCassetteById(@PathVariable Long id){
		return cassetteService.getCassetteById(id);
	}
	
	@PutMapping(value = "/cassette/{id}")
	public ResponseEntity<ResponseStructure<Cassette>> updateCassetteById(@RequestBody Cassette cassette, @PathVariable Long id){
		return cassetteService.updateCassetteById(cassette, id);
	}
	
	@DeleteMapping(value = "/delete/{id}")
	public ResponseEntity<ResponseStructure<String>> deleteCassetteById(@PathVariable Long id){
		return cassetteService.deleteCassetteById(id);
	}
}
