package org.cassettes.cassetteinventorysystem.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cassettes.cassetteinventorysystem.dao.UserDAO;
import org.cassettes.cassetteinventorysystem.entity.ResponseStructure;
import org.cassettes.cassetteinventorysystem.entity.User;
import org.cassettes.cassetteinventorysystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

@Service
public class UserService {
	
	@Autowired
	private UserDAO userDAO;
	
	@Autowired 
	private UserRepository userRepository;
	
	@Value("${media.upload.path}")
    private String uploadPath;
	
	private User getCurrentUser() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if(auth == null || !auth.isAuthenticated()) {
			throw new RuntimeException("Not Authenticated");
		}
        String email = auth.getName();
        User user = userRepository.findByEmail(email);
        System.out.println("=============================");
        System.out.println("USER: " + email);
        if(user == null) {
			throw new RuntimeException("User Cassettes Could Not Be Found");
		}
        return user;
	}
	
	public ResponseEntity<ResponseStructure<User>> updateSpotifyData(String refreshToken){
        User user = getCurrentUser();
        user.setSpotifyRefreshToken(refreshToken);
		
		ResponseStructure<User> structure = new ResponseStructure<>();
		User updatedUser = userDAO.updateSpotifyData(user);
			
		if(updatedUser != null) {
			structure.setData(user);
			structure.setMessage("User Updated Successfully");
			structure.setStatusCode(HttpStatus.OK.value());
			return new ResponseEntity<ResponseStructure<User>>(structure, HttpStatus.OK);
		}
		throw new RuntimeException("User Could Not Be Updated");
	}
	
	public ResponseEntity<ResponseStructure<Boolean>> checkSpotifyData(){
        User user = getCurrentUser();
        String refreshToken = user.getSpotifyRefreshToken();
        Boolean exists = (refreshToken != null && refreshToken.length() > 100);
        
		ResponseStructure<Boolean> structure = new ResponseStructure<>();
			
		structure.setData(exists);
		structure.setMessage("Refresh Token Checked Successfully");
		structure.setStatusCode(HttpStatus.OK.value());
		return new ResponseEntity<ResponseStructure<Boolean>>(structure, HttpStatus.OK);
	}
	
	@Transactional
	public ResponseEntity<ResponseStructure<String>> deleteUser(){
        User user = getCurrentUser();
		
		ResponseStructure<String> structure = new ResponseStructure<>();
		
		boolean exists = userDAO.deleteUser(user.getId());
		if(exists) {
			structure.setData("Deleted Successfully");
			structure.setMessage("User Deleted Successfully");
			structure.setStatusCode(HttpStatus.NO_CONTENT.value());
			
			return new ResponseEntity<ResponseStructure<String>>(structure, HttpStatus.OK);	
		}
		throw new RuntimeException("User Could Not Be Deleted");
	}
	
	@PersistenceContext
    private EntityManager em;
	
	@SuppressWarnings("unchecked")
	public List<String> getAllTitles(){
		List<String> titles = new ArrayList<>();
		
		Query q = em.createQuery("SELECT cassette.title FROM Cassette cassette");
		List<String> titleList = q.getResultList();
		for(String title : titleList) {
			titles.add(title);
		}
			
		return titles;
	}

    public Map<String, Integer> getAllGenreCount() {
        Map<String, Integer> genresCount = new HashMap<>();

        Query q = em.createQuery("SELECT cassette.genre FROM Cassette cassette");
        @SuppressWarnings("unchecked")
		List<List<String>> genreLists = q.getResultList();

        for (List<String> genreList : genreLists) {
            for (String genre : genreList) {
                genresCount.merge(genre, 1, Integer::sum);
            }
        }

        return genresCount;
    }
    
    public Map<String, Integer> getAllStylesCount() {
        Map<String, Integer> stylesCount = new HashMap<>();

        Query q = em.createQuery("SELECT cassette.style FROM Cassette cassette");
        @SuppressWarnings("unchecked")
		List<List<String>> styleLists = q.getResultList();

        for (List<String> styleList : styleLists) {
            for (String style : styleList) {
                stylesCount.merge(style, 1, Integer::sum);
            }
        }

        return stylesCount;
    }
}
