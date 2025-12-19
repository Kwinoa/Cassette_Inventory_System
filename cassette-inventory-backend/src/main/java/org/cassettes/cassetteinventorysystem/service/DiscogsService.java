package org.cassettes.cassetteinventorysystem.service;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cassettes.cassetteinventorysystem.entity.Cassette;
import org.cassettes.cassetteinventorysystem.entity.Track;
// Import Service to bring @Service annotation into scope, indicating that a class is a service in the applications business logic layer
import org.springframework.stereotype.Service;
// Imports WebClient for performing HTTP requests
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class DiscogsService {
	
	// Modern replacement for RestTemplate
	private WebClient webClient;
	
	// Spring Boot automatically provides a pre-configured builder WebClient.Builder
	public DiscogsService(WebClient.Builder webClientBuilder) {
		this.webClient = webClientBuilder
				.baseUrl("https://api.discogs.com")
				.defaultHeader("User-Agent", "CassetteApp/1.0 +@:Kalinathi86@gmail.com") // Adds a user-agent identifier for data collecting purposes for Discogs
				.defaultHeader("Authorization", "Discogs token=MwkNixSbqEgQPzJbxCxboHRjobZduKYEBznPCAKH") // Sets our personal token
				.build(); // finishes constructing the url
	}
	
	public String searchAlbum(String query, int numResults) {
		return webClient.get()
				.uri(uriBuilder -> uriBuilder
				.path("database/search") // the path to the search query as defined by Discogs (/database/search?q={query}&{?type,title,release_title,...)
				.queryParam("q", query) // the query made
				.queryParam("type", "release") // filter for album releases
				.queryParam("format", "cassette")
				.queryParam("per_page", numResults)
				.queryParam("pages", 1)
				.build())	// finishes constructing the url
				.retrieve() // sends the HTTP request, like fetch()
				.bodyToMono(String.class)
				.block(); // converts the HTTP response to a string, which is wrapped in a Mono<String>
	}
	
	
	public String searchMaster(String masterId) {
		return webClient.get()
				.uri(uriBuilder -> uriBuilder
				.path("masters/" + masterId )
				.build())
				.retrieve()
				.bodyToMono(String.class)
				.block();	
	}
	
	// Use jackson to parse JSON string to object
	public List<Cassette> extractCassetteInfo(String response){
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Cassette> cassettes = new HashMap<>();
		try {
			JsonNode root = mapper.readTree(response);
			JsonNode results = root.path("results");
			for(JsonNode cassetteJson : results) {
				
				String masterId = cassetteJson.path("master_id").asText();
				
				if(Integer.parseInt(masterId) != 0 && !cassettes.containsKey(masterId)) {
				JsonNode master = mapper.readTree(searchMaster(masterId));
				JsonNode cover_image = master.path("images").get(0);
				
				Cassette cassette = new Cassette();
				cassette.setTitle(cassetteJson.path("title").asText());
				cassette.setYear(master.path("year").asLong());
				cassette.setFormat("cassette");
				cassette.setCover_image(cover_image.path("resource_url").asText());
				cassette.setDate(LocalDate.now());
				
				JsonNode tracks = master.path("tracklist");
				List<String> track_list = new ArrayList<String>();
				for(JsonNode track : tracks) {
					String newTrack = track.path("position").asText() + " " + track.path("title").asText();
					track_list.add(newTrack);
				}
				cassette.setTrack_List(track_list);
				
				JsonNode genre = master.path("genres");
				List<String> genres = new ArrayList<>();
				if(genre.isArray()) {
					for(JsonNode g : genre)
						genres.add(g.asText());
					cassette.setGenre(genres);
				}
				
				JsonNode style = master.path("styles");
				List<String> styles = new ArrayList<>();
				if(style.isArray()) {
					for(JsonNode s : style)
						styles.add(s.asText());
					cassette.setStyle(styles);
				}
				cassettes.putIfAbsent(masterId, cassette);
				}
			}
		}catch(Exception e){
			System.out.println(e);
		}
		List<Cassette> cassetteList = new ArrayList<>(cassettes.values());
		return cassetteList;
	}

}
