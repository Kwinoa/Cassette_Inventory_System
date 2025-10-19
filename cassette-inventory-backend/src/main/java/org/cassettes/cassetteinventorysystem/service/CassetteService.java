package org.cassettes.cassetteinventorysystem.service;

import java.util.List;
import java.util.Optional;

import org.cassettes.cassetteinventorysystem.dao.CassetteDAO;
import org.cassettes.cassetteinventorysystem.entity.Cassette;
import org.cassettes.cassetteinventorysystem.entity.ResponseStructure;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class CassetteService {
	
	@Autowired
	private CassetteDAO cassetteDAO;
	
	public ResponseEntity<ResponseStructure<Cassette>> addCassettes(Cassette cassette){
		ResponseStructure<Cassette> structure = new ResponseStructure<>();
		
		structure.setData(cassetteDAO.addCassette(cassette));
		structure.setMessage("Cassette Added Sucessfully");
		structure.setStatusCode(HttpStatus.CREATED.value());
		
		return new ResponseEntity<ResponseStructure<Cassette>>(structure, HttpStatus.CREATED);
	}
	
	public ResponseEntity<ResponseStructure<List<Cassette>>> getAllCassettes(){
		ResponseStructure<List<Cassette>> structure = new ResponseStructure<>();
		List<Cassette> cassettes = cassetteDAO.getAllCassettes();
		
		if(!cassettes.isEmpty()) {
			structure.setData(cassettes);
			structure.setMessage("Cassettes Found");
			structure.setStatusCode(HttpStatus.OK.value());
			
			return new ResponseEntity<ResponseStructure<List<Cassette>>>(structure, HttpStatus.OK);
		}
		throw new RuntimeException("Cassettes Could Not Found");
	}
	
	public ResponseEntity<ResponseStructure<Cassette>> getCassetteById(int id){
		ResponseStructure<Cassette> structure = new ResponseStructure<>();
		Optional<Cassette> cassettes = cassetteDAO.getCassetteById(id);
		
		if(cassettes.isPresent()) {
			structure.setData(cassettes.get());
			structure.setMessage("Cassette With id: " + id + " Found Successfully");
			structure.setStatusCode(HttpStatus.ACCEPTED.value());
			return new ResponseEntity<ResponseStructure<Cassette>>(structure, HttpStatus.ACCEPTED);
		}
		throw new RuntimeException("Cassette With id: " + id + "Could Not Be Found");		
	}
	
	public ResponseEntity<ResponseStructure<Cassette>> updateCassetteById(Cassette cassette, int id){
		ResponseStructure<Cassette> structure = new ResponseStructure<>();
		Optional<Cassette> cassettes = cassetteDAO.getCassetteById(id);
		
		if(cassettes.isPresent()) {
			Cassette existingCa= cassettes.get();
			existingCa.setTitle(cassette.getTitle());
			existingCa.setYear(cassette.getYear());
			existingCa.setLabel(cassette.getLabel());
			existingCa.setFormat(cassette.getFormat());
			existingCa.setThumb(cassette.getThumb());
			existingCa.setUrl(cassette.getUrl());
			
			Cassette updatedCassette =cassetteDAO.addCassette(existingCa);
			
			structure.setData(updatedCassette);
			structure.setMessage("Cassettes With id: " + id + " Updated Successfully");
			structure.setStatusCode(HttpStatus.OK.value());
			return new ResponseEntity<ResponseStructure<Cassette>>(structure, HttpStatus.OK);
		}
		throw new RuntimeException("Cassette With id: " + id + "Could Not Be Updated");
	}
	
	public ResponseEntity<ResponseStructure<String>> deleteCassetteById(int id){
		ResponseStructure<String> structure = new ResponseStructure<>();
		Optional<Cassette> cassettes = cassetteDAO.getCassetteById(id);
		
		if(cassettes.isPresent()) {
			cassetteDAO.deleteById(id);
			structure.setData("Delete Successfully");
			structure.setMessage("Cassettes with id: " + id + "Deleted Successfully");
			structure.setStatusCode(HttpStatus.NO_CONTENT.value());
			
			return new ResponseEntity<ResponseStructure<String>>(structure, HttpStatus.NO_CONTENT);
		}
		throw new RuntimeException("Cassette With id: " + id + " Could Not Be Deleted");
	}
}
