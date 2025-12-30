package org.cassettes.cassetteinventorysystem.controller;

import java.util.Map;

import org.cassettes.cassetteinventorysystem.entity.Cassette;
import org.cassettes.cassetteinventorysystem.entity.ResponseStructure;
import org.cassettes.cassetteinventorysystem.service.SpotifyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SpotifyController {

    @Autowired
    private SpotifyService spotifyService;

    @PostMapping("/spotify/callback")
    public ResponseEntity<ResponseStructure<Map<String, Object>>> handleCallback(@RequestBody Map<String, String> request) {
        String code = request.get("code");
        String codeVerifier = request.get("codeVerifier");

        return spotifyService.processSpotifyToken(code, codeVerifier);

    }
    
    @PostMapping("/spotify/callback/refresh")
    public ResponseEntity<ResponseStructure<Map<String, Object>>> getTokenUsingRefresh() {
        return spotifyService.processSpotifyTokenUsingRefresh();
    }
    
    @PutMapping("/spotify/transfer")
    public ResponseEntity<ResponseStructure<Map<String, Object>>> transferSpotifyPlayback(@RequestBody Map<String, Object> request, @RequestHeader("Authorization") String authHeader){
    	
    	return spotifyService.processTransferPlayback(authHeader, request);
    }
    
    @PostMapping("/spotify/setUri")
    public ResponseEntity<ResponseStructure<Cassette>> searchSpotifySong(@RequestBody Map<String, String> request) {
        Long id = Long.parseLong(request.get("cassetteId"));
    	String accessToken = request.get("accessToken");
        String artist = request.get("artist");
        String album = request.get("album");
        
        System.out.println("SEARCH QUERY: " + artist + " " + album);

        return spotifyService.processAlbumUri(id, accessToken, artist, album);
    }
    
    @PutMapping("/spotify/changeAlbum/{deviceId}")
    public ResponseEntity<ResponseStructure<Map<String, Object>>> changeSpotifyAlbum(@PathVariable String deviceId, @RequestBody Map<String, String> request) {
    	String albumUri = request.get("albumUri");
        String accessToken = request.get("accessToken");

        return spotifyService.processAlbumChange(albumUri, accessToken, deviceId);
    }
    
    @PostMapping("/spotify/shuffleOff/{deviceId}")
    public ResponseEntity<ResponseStructure<String>> changeShuffleState(@PathVariable String deviceId, @RequestBody Map<String, String> request) {
        String accessToken = request.get("accessToken");

        return spotifyService.processShuffle(accessToken, deviceId);
    }
}
