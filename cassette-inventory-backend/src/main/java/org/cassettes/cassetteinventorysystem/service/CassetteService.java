package org.cassettes.cassetteinventorysystem.service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.cassettes.cassetteinventorysystem.dao.CassetteDAO;
import org.cassettes.cassetteinventorysystem.entity.Cassette;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

@Service
public class CassetteService {
	
	@Autowired
	private CassetteDAO cassetteDAO;
	
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
	
	public ResponseEntity<ResponseStructure<Cassette>> addCassettes(Cassette cassette){
        User user = getCurrentUser();
        
		cassette.setUser(user);
		
		ResponseStructure<Cassette> structure = new ResponseStructure<>();

		structure.setData(cassetteDAO.addCassette(cassette));
		structure.setMessage("Cassette Added Sucessfully");
		structure.setStatusCode(HttpStatus.CREATED.value());
		
		return new ResponseEntity<ResponseStructure<Cassette>>(structure, HttpStatus.CREATED);
	}
	
	public ResponseEntity<ResponseStructure<String>> uploadCoverImage(@RequestParam("file") MultipartFile file){
		ResponseStructure<String> structure = new ResponseStructure<>();
		
		try {
            // Create directory if it doesn’t exist
            Path folderPath = Paths.get(uploadPath);
            if (!Files.exists(folderPath)) {
                Files.createDirectories(folderPath);
            }

            // Unique filename
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path filePath = folderPath.resolve(fileName);

            // Save file
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // URL to serve file
            String fileUrl = "/media/" + fileName;

            structure.setMessage("Image uploaded successfully");
            structure.setData(fileUrl);
            structure.setStatusCode(HttpStatus.OK.value());

            return new ResponseEntity<>(structure, HttpStatus.OK);

        } catch (Exception e) {
            structure.setMessage("Image upload failed: " + e.getMessage());
            structure.setData(null);
            structure.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());

            return new ResponseEntity<>(structure, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
	
	
//	public ResponseEntity<ResponseStructure<List<Cassette>>> getAllCassettes(){
//		ResponseStructure<List<Cassette>> structure = new ResponseStructure<>();
//		List<Cassette> cassettes = cassetteDAO.getAllCassettes();
//		
//		if(!cassettes.isEmpty()) {
//			structure.setData(cassettes);
//			structure.setMessage("Cassettes Found");
//			structure.setStatusCode(HttpStatus.OK.value());
//			
//			return new ResponseEntity<ResponseStructure<List<Cassette>>>(structure, HttpStatus.OK);
//		}
//		throw new RuntimeException("Cassettes Could Not Found");
//	}
	
	public ResponseEntity<ResponseStructure<List<Cassette>>> getUserCassettes(){
        User user = getCurrentUser();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("AUTH CLASS = " + auth.getClass());
        System.out.println("AUTH NAME  = " + auth.getName());
		ResponseStructure<List<Cassette>> structure = new ResponseStructure<>();
		
		List<Cassette> cassettes = cassetteDAO.getCassettesByUserId(user.getId());;
		
		if(!cassettes.isEmpty()) {
			structure.setData(cassettes);
			structure.setMessage("User Cassettes Found");
			structure.setStatusCode(HttpStatus.OK.value());
			
			return new ResponseEntity<ResponseStructure<List<Cassette>>>(structure, HttpStatus.OK);
		}
		throw new RuntimeException("User Cassettes Could Not Found");
	}
	
	public ResponseEntity<ResponseStructure<Cassette>> getCassetteById(Long id){
        User user = getCurrentUser();
		
		ResponseStructure<Cassette> structure = new ResponseStructure<>();
		Cassette cassettes = cassetteDAO.getCassetteByIdAndUserId(id, user.getId());
		
		if(cassettes != null) {
			structure.setData(cassettes);
			structure.setMessage("Cassette With id: " + id + " Found Successfully");
			structure.setStatusCode(HttpStatus.ACCEPTED.value());
			return new ResponseEntity<ResponseStructure<Cassette>>(structure, HttpStatus.ACCEPTED);
		}
		throw new RuntimeException("Cassette With id: " + id + "Could Not Be Found");		
	}
	
	public ResponseEntity<ResponseStructure<Cassette>> updateCassetteById(Cassette cassette, Long id){
        User user = getCurrentUser();
        
        Cassette original = cassetteDAO.getCassetteByIdAndUserId(id, user.getId());        
		original.setTitle(cassette.getTitle());
		original.setDate(cassette.getDate());
		original.setGenre(cassette.getGenre());
		original.setStyle(cassette.getStyle());
		original.setYear(cassette.getYear());
		original.setCover_image(cassette.getCover_image());
		
		ResponseStructure<Cassette> structure = new ResponseStructure<>();
		Cassette updatedCassette = cassetteDAO.updateCassette(original);
			
		if(updatedCassette != null) {
			structure.setData(updatedCassette);
			structure.setMessage("Cassette With id: " + id + " Updated Successfully");
			structure.setStatusCode(HttpStatus.OK.value());
			return new ResponseEntity<ResponseStructure<Cassette>>(structure, HttpStatus.OK);
		}
		throw new RuntimeException("Cassette With id: " + id + "Could Not Be Updated");
	}
	
	@Transactional
	public ResponseEntity<ResponseStructure<String>> deleteCassetteById(Long id){
        User user = getCurrentUser();
		
		ResponseStructure<String> structure = new ResponseStructure<>();
		
		boolean exists = cassetteDAO.deleteByIdAndUserId(id, user.getId());
		if(exists) {
			structure.setData("Delete Successfully");
			structure.setMessage("Cassette with id: " + id + "Deleted Successfully");
			structure.setStatusCode(HttpStatus.NO_CONTENT.value());
			
			return new ResponseEntity<ResponseStructure<String>>(structure, HttpStatus.OK);	
		}
		throw new RuntimeException("Cassette With id: " + id + " Could Not Be Deleted");
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
