package org.cassettes.cassetteinventorysystem.service;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cassettes.cassetteinventorysystem.entity.Cassette;
import org.cassettes.cassetteinventorysystem.entity.ResponseStructure;
import org.cassettes.cassetteinventorysystem.entity.User;
import org.cassettes.cassetteinventorysystem.repository.CassetteRepository;
import org.cassettes.cassetteinventorysystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ResponseStatusException;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import reactor.netty.http.client.HttpClient;

@Service
public class SpotifyService {

    @Value("${spotify.client.id}")
    private String clientId;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private CassetteRepository cassetteRepository;
    
    private final WebClient webClientAuthorize;
    private final WebClient webClient;
    
    public SpotifyService(WebClient.Builder webClientAuthorizeBuilder, WebClient.Builder webClientBuilder) {
    	this.webClientAuthorize = webClientAuthorizeBuilder
    			.baseUrl("https://accounts.spotify.com")
    			.build();
    	
    	HttpClient httpClient = HttpClient.create()
    		    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
    		    .doOnConnected(conn -> 
    		        conn.addHandlerLast(new ReadTimeoutHandler(5))
    		            .addHandlerLast(new WriteTimeoutHandler(5)))
    		    .responseTimeout(Duration.ofSeconds(5));
    	
    	this.webClient = webClientBuilder
    			.clientConnector(new ReactorClientHttpConnector(httpClient))
    			.baseUrl("https://api.spotify.com/v1")
    			.build();
    			
    }
    
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

    public Map<String, Object> getSpotifyToken(String code, String codeVerifier) {
    	System.out.println("code:" + code);
    	System.out.println("code verifier: " + codeVerifier);
    	try {
            return webClientAuthorize.post()
                    .uri("/api/token")
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                    .bodyValue(buildBody(code, codeVerifier))
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>(){})
                    .block();
        } catch (WebClientResponseException e) {
            // Log the actual Spotify error body to your console!
            System.err.println("Spotify Auth Error: " + e.getResponseBodyAsString());
            throw e; 
        }
    }
    
    public Map<String, Object> getSpotifyTokenUsingRefresh(String refreshToken) {
    	try {
    	return webClientAuthorize.post()
    			.uri("/api/token")
    			.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    			.bodyValue(buildBody2(refreshToken))
    			.retrieve()
    			.bodyToMono(new ParameterizedTypeReference<Map<String, Object>>(){})
    			.block();
    	} catch (WebClientResponseException e) {
    		System.out.println("Spotify rejected the request: " + e.getResponseBodyAsString());
    		throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Spotify session expired");
    	}
    }
    
    public Map<String, Object> transferPlayback(String accessToken, Map<String, Object> request) {
    	try {
    	return webClient.put()
    			.uri("/me/player")
    			.header(HttpHeaders.AUTHORIZATION, accessToken)
    			.contentType(MediaType.APPLICATION_JSON)
    			.bodyValue(request)
    			.retrieve()
    			.bodyToMono(new ParameterizedTypeReference<Map<String, Object>>(){})
    			.block();
    	}catch (WebClientResponseException e) {
    		System.out.println("Spotify rejected the request: " + e.getResponseBodyAsString());
    		throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "");
    	}
    }
    
    
    public Map<String, Object> searchSpotifyAlbum(String accessToken, String artist, String album) {
    	return webClient.get()
    			.uri(uriBuilder -> uriBuilder
    			.path("/search")
    			.queryParam("q", artist + " " + album)
    			.queryParam("type", "album")
    			.queryParam("market", "US")
    			.queryParam("limit", "1")
    			.build())
    			.header("Authorization", "Bearer " + accessToken)
    			.retrieve()
    			.bodyToMono(new ParameterizedTypeReference<Map<String, Object>>(){})
    			.block();
    }
    
    public Map<String, Object> changeAlbum(String albumUri, String accessToken, String deviceId) {
    	try {
        	return webClient.put()
        			.uri(uriBuilder -> uriBuilder
        					.path("/me/player/play/")
        					.queryParam("device_id", deviceId)
        					.build()
        				)
        			.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
        			.contentType(MediaType.APPLICATION_JSON)
        			.bodyValue(buildBody3(albumUri))
        			.retrieve()
        			.bodyToMono(new ParameterizedTypeReference<Map<String, Object>>(){})
        			.timeout(Duration.ofSeconds(5))
        			.block();
        	}catch (WebClientResponseException e) {
        		System.out.println("Spotify rejected the request: " + e.getResponseBodyAsString());
        		throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "");
        	}
    }
    
    public void setShuffleOff(String accessToken, String deviceId) {
        String authHeader = accessToken.startsWith("Bearer ") ? accessToken : "Bearer " + accessToken;

        try {
            webClient.put()
                .uri(uriBuilder -> uriBuilder
                    .path("/me/player/shuffle")
                    .queryParam("state", false) // false = shuffle off, true = shuffle on
                    .queryParam("device_id", deviceId)
                    .build())
                .header(HttpHeaders.AUTHORIZATION, authHeader)
                .retrieve()
                .toBodilessEntity()
                .block();
            System.out.println("Shuffle turned off successfully.");
        } catch (WebClientResponseException e) {
            System.err.println("Failed to set shuffle: " + e.getResponseBodyAsString());
        }
    }
    
    private MultiValueMap<String, String> buildBody(String code, String codeVerifier) {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("code", code);
        body.add("redirect_uri", "http://127.0.0.1:3000");
        body.add("client_id", clientId);
        body.add("code_verifier", codeVerifier);
        return body;
    }
    
    private MultiValueMap<String, String> buildBody2(String refreshToken) {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "refresh_token");
        body.add("client_id", clientId);
        body.add("refresh_token", refreshToken);
        return body;
    }
    
    private Map<String, Object> buildBody3(String albumUri) {
    	Map<String, Integer> offset = new HashMap<>();
    	offset.put("position", 0);
    	
        Map<String, Object> body = new HashMap<>();
        body.put("context_uri", "spotify:album:" + albumUri);
        body.put("offset", offset);
        body.put("position_ms", 0);
        return body;
    }
    
    public ResponseEntity<ResponseStructure<Cassette>> processAlbumUri(Long id, String accessToken, String artist, String album){
    	User user = getCurrentUser();
		Cassette cassette = cassetteRepository.findByIdAndUser_Id(id, user.getId());
		ResponseStructure<Cassette> structure = new ResponseStructure<>();

    	try {
	    	Map<String, Object> response = searchSpotifyAlbum(accessToken, artist, album);
	    	response.forEach((key, value) -> System.out.println("RESPONSE: " +  key + ": " + value));
	    	Map<String, Object> albums = (Map<String, Object>) response.get("albums");
	    	if((Integer) albums.get("total") == 0) {
	    		cassette.setAlbumUri("0");
	    		cassetteRepository.save(cassette);
	    		
	    		structure.setStatusCode(HttpStatus.OK.value());
	    		structure.setMessage("Spotify Album Uri Not Found");
	    		structure.setData(cassette);
	    		
	    		return new ResponseEntity<>(structure, HttpStatus.OK);
	    	}else {
	    		albums.forEach((key, value) -> System.out.println("ALBUMS:" + key + ": " + value));
	    		List<Map<String, Object>> items = (List<Map<String, Object>>) albums.get("items");
	    		System.out.println("ITEMS: " + items);
	    		Map<String, Object> albumInfo = (Map<String, Object>) items.get(0);
	    		String uriString = (String) albumInfo.get("uri");
	    		System.out.println("URI STRING: " + uriString);
	    		String[] uriList = uriString.split(":");
	    		String uri = uriList[2];
	    		
//	    	if(cassette.getTrack_List().size() == (Integer)albumInfo.get("total_tracks")) {
	    		System.out.println("ALBUM URI: " + uriList[2]);
	    		cassette.setAlbumUri(uri);
	    		cassetteRepository.save(cassette);
//	    	}
	    		
	    		structure.setStatusCode(HttpStatus.OK.value());
	    		structure.setMessage("Spotify Album Uri Retrieved and Set Successfully");
	    		structure.setData(cassette);
	    		
	    		return new ResponseEntity<>(structure, HttpStatus.OK);
	    	}
    	} catch(Exception e) {
    		e.printStackTrace();
    	}
    	structure.setStatusCode(HttpStatus.OK.value());
		structure.setMessage("Spotify Album Uri Not Found");
		structure.setData(cassette);
		
		return new ResponseEntity<>(structure, HttpStatus.OK);
    }
    
    public ResponseEntity<ResponseStructure<Map<String, Object>>> processAlbumChange(String albumUri, String accessToken, String deviceId){
        Map<String, Object> tokenData = changeAlbum(albumUri, accessToken, deviceId);
        System.out.println("DEBUG: Sending to Spotify -> " + albumUri);

        ResponseStructure<Map<String, Object>> structure = new ResponseStructure<>();
        structure.setStatusCode(HttpStatus.OK.value());
        structure.setMessage("Spotify Playback Album Successfully Changed");
        structure.setData(tokenData);

        return new ResponseEntity<>(structure, HttpStatus.OK);
   }
    
    public ResponseEntity<ResponseStructure<Map<String, Object>>> processSpotifyToken(String code, String codeVerifier){
         Map<String, Object> tokenData = getSpotifyToken(code, codeVerifier);

         ResponseStructure<Map<String, Object>> structure = new ResponseStructure<>();
         structure.setStatusCode(HttpStatus.OK.value());
         structure.setMessage("Spotify Token Acquired");
         structure.setData(tokenData);

         return new ResponseEntity<>(structure, HttpStatus.OK);
    }
    
    public ResponseEntity<ResponseStructure<Map<String, Object>>> processSpotifyTokenUsingRefresh(){
    	User user = getCurrentUser();
    	String refreshToken = user.getSpotifyRefreshToken();
    	System.out.println("SPOTIFY TOKEN --------------");
    	System.out.println(refreshToken);

        Map<String, Object> tokenData = getSpotifyTokenUsingRefresh(refreshToken);
        ResponseStructure<Map<String, Object>> structure = new ResponseStructure<>();


        if(tokenData != null && tokenData.containsKey("refresh_token")) {
        	String newRefreshToken = (String) tokenData.get("refresh_token");
            user.setSpotifyRefreshToken(newRefreshToken);
            userRepository.save(user); // Persistence!
            structure.setMessage("Spotify Token Acquired Using Refresh");
        }else if(tokenData != null && !tokenData.containsKey("refresh_token")){
            structure.setMessage("Spotify Token Does Not Require Refresh");
        }
        
        structure.setStatusCode(HttpStatus.OK.value());
        structure.setData(tokenData);

        return new ResponseEntity<ResponseStructure<Map<String, Object>>>(structure, HttpStatus.OK);
	}
    
    public ResponseEntity<ResponseStructure<Map<String, Object>>> processTransferPlayback(String accessToken, Map<String, Object> request){
        transferPlayback(accessToken, request);

        ResponseStructure<Map<String, Object>> structure = new ResponseStructure<>();
        structure.setStatusCode(HttpStatus.OK.value());
        structure.setMessage("Playback transferred to web player");
        structure.setData(null);

        return new ResponseEntity<ResponseStructure<Map<String, Object>>>(structure, HttpStatus.OK);
	}
    
    public ResponseEntity<ResponseStructure<String>> processShuffle(String accessToken, String deviceId){
        setShuffleOff(accessToken, deviceId);

        ResponseStructure<String> structure = new ResponseStructure<>();
        structure.setStatusCode(HttpStatus.OK.value());
        structure.setMessage("Spotify Playback Successfully Paused");
        structure.setData("");

        return new ResponseEntity<>(structure, HttpStatus.OK);
   }
}
