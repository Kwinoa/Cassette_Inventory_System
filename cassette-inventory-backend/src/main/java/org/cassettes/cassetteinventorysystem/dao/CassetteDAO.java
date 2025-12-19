package org.cassettes.cassetteinventorysystem.dao;

import java.util.List;
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
	
//	public List<Cassette> getAllCassettes(){
//		return cassetteRepository.findAll();
//	}
	
	public List<Cassette> getCassettesByUserId(Long userId){
		List<Cassette> list = cassetteRepository.findByUser_Id(userId);
		System.out.println("FOUND CASSETTES: " + list.size());
		return list;
	}
	
	public Cassette getCassetteByIdAndUserId(Long id, Long userId){
		return cassetteRepository.findByIdAndUser_Id(id, userId);
	}
	
	public Cassette updateCassette(Cassette cassette) {		
		return cassetteRepository.save(cassette);
	}
	
	public boolean deleteByIdAndUserId(Long id, Long userId) {
	    boolean exists = cassetteRepository.existsByIdAndUser_Id(id, userId);
		
		if(exists) {
			cassetteRepository.deleteByIdAndUser_Id(id, userId);
			return true;
		}
		return false;
	}
}
