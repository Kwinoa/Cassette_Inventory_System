package org.cassettes.cassetteinventorysystem.dao;

import java.util.List;
import java.util.Optional;

import org.cassettes.cassetteinventorysystem.entity.Cassette;
import org.cassettes.cassetteinventorysystem.repository.CassetteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class CassetteDAO {
	
	@Autowired
	private CassetteRepository cassetteRepository;
	
	public Cassette addCassette(Cassette cassette) {
		return cassetteRepository.save(cassette);
	}
	
	public List<Cassette> getAllCassettes(){
		return cassetteRepository.findAll();
	}
	
	public Optional<Cassette> getCassetteById(int id){
		return cassetteRepository.findById(id);
	}
	
	public Cassette updateCassetteById(Cassette cassette) {
		return cassetteRepository.save(cassette);
	}
	
	public boolean deleteById(int id) {
		Optional<Cassette> cassettes = getCassetteById(id);
		
		if(cassettes.isPresent()) {
			cassetteRepository.delete(cassettes.get());
			return true;
		}
		return false;
	}
}
