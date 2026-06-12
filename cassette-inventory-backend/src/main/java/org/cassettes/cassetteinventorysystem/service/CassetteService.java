package org.cassettes.cassetteinventorysystem.service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.cassettes.cassetteinventorysystem.dao.CassetteDAO;
import org.cassettes.cassetteinventorysystem.entity.Cassette;
import org.cassettes.cassetteinventorysystem.entity.Month;
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
	private DiscogsService discogsService;
	
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

        if(user == null) {
			throw new RuntimeException("User Cassettes Could Not Be Found");
		}
        return user;
	}
	
	public Cassette addCassettes(Cassette cassette){
		Cassette cassetteWithName = discogsService.getCassetteTitle(cassette);
		
        User user = getCurrentUser();
        cassetteWithName.setUser(user);
        
        int monthNum = LocalDate.now().getMonthValue();
        List<Month> month_data = user.getMonth_list();
        for(Month m : month_data) {
        	if(m.getMonth() == monthNum) {
        		m.setCassette_count(m.getCassette_count() + 1);
        		System.out.println("Incrementing value for month: " + monthNum);
        	}
        }
        user.setMonth_list(month_data);
        
		return cassetteDAO.addCassette(cassetteWithName);
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
	
	public ResponseEntity<ResponseStructure<List<Cassette>>> getUserCassettes(){
        User user = getCurrentUser();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        if(auth == null || !auth.isAuthenticated()) {
			throw new RuntimeException("Not Authenticated");
		}

		ResponseStructure<List<Cassette>> structure = new ResponseStructure<>();
		
		List<Cassette> cassettes = cassetteDAO.getCassettesByUserId(user.getId());;
		
		if(!cassettes.isEmpty()) {
			structure.setData(cassettes);
			structure.setMessage("User Cassettes Found");
			structure.setStatusCode(HttpStatus.OK.value());
			
			return new ResponseEntity<ResponseStructure<List<Cassette>>>(structure, HttpStatus.OK);
		}
		else {
			structure.setData(cassettes);
			structure.setMessage("User Cassetttes Not Found");
			structure.setStatusCode(HttpStatus.OK.value());
			
			return new ResponseEntity<ResponseStructure<List<Cassette>>>(structure, HttpStatus.OK);

		}
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
        
        if(original.getDate() != cassette.getDate()) {
        	int oldMonthNum = original.getDate().getMonthValue();
        	int newMonthNum = cassette.getDate().getMonthValue();
        	List<Month> month_data = user.getMonth_list();
        	for(Month m : month_data) {
        		if(m.getMonth() == newMonthNum) {
        			m.setCassette_count(m.getCassette_count() + 1);
        		}
        		if(m.getMonth() == oldMonthNum) {
        			m.setCassette_count(m.getCassette_count() - 1);
        		}
        	} 
            user.setMonth_list(month_data);
        }
        
		original.setTitle(cassette.getTitle());
		original.setDate(cassette.getDate());
		original.setGenre(cassette.getGenre());
		original.setStyle(cassette.getStyle());
		original.setYear(cassette.getYear());
		original.setCover_image(cassette.getCover_image());
		original.setResource_url(cassette.getResource_url());
		original.setTrack_list_size(cassette.getTrack_list_size());
		
		
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
		
		Cassette cassette = cassetteDAO.getCassetteByIdAndUserId(id, user.getId());
		
		boolean exists = cassetteDAO.deleteByIdAndUserId(id, user.getId());
		if(exists) {
			int monthNum = cassette.getDate().getMonthValue();
			List<Month> month_data = user.getMonth_list();
			for(Month m : month_data) {
				if(m.getMonth() == monthNum) {
					m.setCassette_count(m.getCassette_count() - 1);
				}
			}
	        user.setMonth_list(month_data);

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
    
    public Map<String, Object> getStatsForUser() {
    	User user = getCurrentUser();
        List<Cassette> userTapes = getUserCassettes().getBody().getData();
        Map<String, Object> stats = new HashMap<>();
        
        // Month distribution of cassettes
        stats.put("monthDistribution", user.getMonth_list());

        // Total Count
        stats.put("totalCassettes", userTapes.size());
        
        // Years Joined
        LocalDate today = LocalDate.now();
        LocalDate joinDate = user.getJoinDate();
        int years = today.getYear() - joinDate.getYear();
        float months = (float) (((float)today.getMonthValue() -  (float)joinDate.getMonthValue())/12.0);
        System.out.print("join month: " + joinDate.getMonthValue() + "calculated months: " + months);
        System.out.println("years: " + years + " months: " + months);
        stats.put("yearsSinceJoined", years + months);
        if(!userTapes.isEmpty()) {
	
	    	// Genre Distribution (Counting occurrences in the List<String> genre)
	        Map<String, Long> genreDist = userTapes.stream()
	                .flatMap(c -> c.getGenre().stream())
	                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
	        stats.put("genreDistribution", genreDist);
	        
	        // Style Distribution
	        Map<String, Long> styleDist = userTapes.stream()
	                .flatMap(c -> c.getStyle().stream())
	                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
	        stats.put("styleDistribution", styleDist);
	        
	        // Largest Cassette
	        Optional<Cassette> biggestTape = userTapes.stream()
	        		.max(Comparator.comparingInt(Cassette::getTrack_list_size));
	        stats.put("largestCassette", biggestTape.get().getTitle());
     
        }else {
        	Map<String, Long> empty = new HashMap<String, Long>();
        	empty.put("No Data", (long) 1);
        	stats.put("genreDistribution", empty);
        	stats.put("styleDistribution", empty);
        	stats.put("largestCassette", "N/A");
        }
        
        // Favorite Artist (The "name" field in your entity)
        String favArtist = userTapes.stream()
                .collect(Collectors.groupingBy(Cassette::getName, Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("N/A");
        stats.put("favoriteArtist", favArtist);
        
        // Favorite Decade
        Map<String, Long> decadeDist = userTapes.stream()
            .collect(Collectors.groupingBy(
                c -> (c.getYear() / 10 * 10) + "s", 
                Collectors.counting()
            ));
        
        String favDecade = decadeDist.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse("N/A");
        
        stats.put("favoriteDecade", favDecade);
	        
        return stats;
    }
}
