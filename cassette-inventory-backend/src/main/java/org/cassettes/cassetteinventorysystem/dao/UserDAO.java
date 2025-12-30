package org.cassettes.cassetteinventorysystem.dao;

import java.util.List;
import org.cassettes.cassetteinventorysystem.entity.Cassette;
import org.cassettes.cassetteinventorysystem.entity.User;
import org.cassettes.cassetteinventorysystem.repository.CassetteRepository;
import org.cassettes.cassetteinventorysystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class UserDAO {
	
	@Autowired
	private UserRepository userRepository;
	
	
	public User updateSpotifyData(User user) {		
		return userRepository.save(user);
	}
	
	public boolean deleteUser(Long userId) {
	    boolean exists = userRepository.existsById(userId);
		
		if(exists) {
			userRepository.deleteById(userId);
			return true;
		}
		return false;
	}
}
