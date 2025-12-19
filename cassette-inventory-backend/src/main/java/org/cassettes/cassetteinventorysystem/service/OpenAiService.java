package org.cassettes.cassetteinventorysystem.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.cassettes.cassetteinventorysystem.controller.DiscogsController;
import org.cassettes.cassetteinventorysystem.entity.Cassette;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.openai.client.OpenAIClient;
import com.openai.models.ChatModel;
import com.openai.models.responses.ResponseCreateParams;

@Service
public class OpenAiService {
	
	OpenAIClient openAiClient;
	DiscogsController discogsController;
	CassetteService cassetteService;

	@Autowired
	public OpenAiService(OpenAIClient openAiClient, CassetteService cassetteService, DiscogsController discogsController) {
		this.openAiClient = openAiClient;
		this.cassetteService = cassetteService;
		this.discogsController = discogsController;
	}
	
	
	public String createPromptRecommendations() {
		String prompt = "Recommend 10 albums with confirmed Discogs cassette releases. \r\n"
				+ "\r\n"
				+ "Context:\r\n"
				+ "- Owned List: [Insert Titles]\r\n"
				+ "- Weighted Genres: [Insert Genre:Count]\r\n"
				+ "- Weighted Styles: [Insert Style:Count]\r\n"
				+ "\r\n"
				+ "Constraints:\r\n"
				+ "1. Similarity: Base recommendations on owned titles, prioritizing genres/styles with higher counts.\r\n"
				+ "2. Format: Return a raw numbered list on separate lines.\r\n"
				+ "3. Filter: Exclude titles already in the Owned List.\r\n"
				+ "4. Output: No intro, no explanation. Just the lines";
		
		List<String> titles = cassetteService.getAllTitles();
		prompt += " Here are the titles: ";
		for(String title : titles) {
			prompt += title + ", ";
		}
		
		Map<String, Integer> genres = cassetteService.getAllGenreCount();
		prompt += ". Here are the genres: ";
		for(Map.Entry<String, Integer> entry : genres.entrySet()) {
			prompt += entry.getKey() + ":" + entry.getValue() + ", ";
		}
		
		Map<String, Integer> styles = cassetteService.getAllStylesCount();
		prompt += ". Here are the styles: ";
		for(Map.Entry<String, Integer> entry : styles.entrySet()) {
			prompt += entry.getKey() + ":" + entry.getValue() + ", ";
		}
		
		System.out.println(prompt);
		return prompt;
	}
	
//	public String createPromptMixtape() {
//		String prompt = "I am trying to make a 12 track cassette mixtape. Given that the tracks have to be titles that have a cassette"
//				+ "physical format that was sold in the past or in the present, and given that I will provide a list of titles for cassettes that are already owned, "
//				+ "a list of music genres from the cassettes in the format of \"genre:cassettes with genre\", and a similar list of music styles from the cassettes"
//				+ "in the format of \"style:cassettes with style\". The genres and styles that are in more cassettes should be considered more relevant/important."
//				+ "Give me a list [] of 5 cassettes in the format of \"artist - title\" that are similar to the given titles, genres, and styles, or that people "
//				+ "with similar tastes listen to. No explanation, just a list in that format.";
//		
//		List<String> titles = cassetteService.getAllTitles();
//		prompt += " Here are the titles: ";
//		for(String title : titles) {
//			prompt += title + ", ";
//		}
//		
//		Map<String, Integer> genres = cassetteService.getAllGenreCount();
//		prompt += ". Here are the genres: ";
//		for(Map.Entry<String, Integer> entry : genres.entrySet()) {
//			prompt += entry.getKey() + ":" + entry.getValue();
//		}
//		
//		Map<String, Integer> styles = cassetteService.getAllStylesCount();
//		prompt += ". Here are the styles: ";
//		for(Map.Entry<String, Integer> entry : styles.entrySet()) {
//			prompt += entry.getKey() + ":" + entry.getValue();
//		}
//		
//		return prompt;
//	}


	public String[] getRecommendations() {
		String[] recommendationsList = null;
		String promptRecommendations = createPromptRecommendations();
		ResponseCreateParams createParams = ResponseCreateParams.builder()
		            .input(promptRecommendations)
		            .model(ChatModel.GPT_4O)
		            .build();
	
	   String recommendations = openAiClient.responses().create(createParams).output().stream()
	            .flatMap(item -> item.message().stream())
	            .flatMap(message -> message.content().stream())
	            .flatMap(content -> content.outputText().stream())
	            .map(outputText -> outputText.text())
	            .collect(Collectors.joining("\n"));
	   
	   System.out.println(promptRecommendations);
	   System.out.println(recommendations);
	    
	   recommendationsList = recommendations.split("\\d\\.");
	   System.out.println(recommendationsList.length);
	   for(String s : recommendationsList) {
		   System.out.println(s);
	   }
	    	    
	   return recommendationsList;
	}
	
	public List<Cassette> getCassetteRecommendations(){
		List<Cassette> recommendedCassettes = new ArrayList<>();
		
		String[] recommendationsList = getRecommendations();
		for(String title : recommendationsList) {
			Cassette cassette = discogsController.searchAlbum(title).getBody().getData();	
			if(cassette != null)
				recommendedCassettes.add(cassette);		
		}
		System.out.println(recommendedCassettes.size());
		return recommendedCassettes;
	}
	
//	public List<String> getMixtape(){
//		List<String> mixtape = new ArrayList<String>();
//		ResponseCreateParams createParams = ResponseCreateParams.builder()
//		            .input(createPromptMixtape())
//		            .model(ChatModel.GPT_4O)
//		            .build();
//	
//	    openAiClient.responses().create(createParams).output().stream()
//	            .flatMap(item -> item.message().stream())
//	            .flatMap(message -> message.content().stream())
//	            .flatMap(content -> content.outputText().stream())
//	            .forEach(outputText -> mixtape.add(outputText.text()));
//	    
//	   return mixtape;
//	}
}

