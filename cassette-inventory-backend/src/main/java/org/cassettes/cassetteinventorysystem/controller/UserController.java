package org.cassettes.cassetteinventorysystem.controller;

import java.util.Map;

import org.cassettes.cassetteinventorysystem.entity.ResponseStructure;
import org.cassettes.cassetteinventorysystem.entity.User;
import org.cassettes.cassetteinventorysystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
	@Autowired
	private UserService userService;
	
	@PatchMapping(value = "/updateRefreshToken")
	public ResponseEntity<ResponseStructure<User>> updateSpotifyData(@RequestBody Map<String, String> request){
		String refreshToken = request.get("refreshToken");
		return userService.updateSpotifyData(refreshToken);
	}
	
	@GetMapping(value = "/checkRefreshToken")
	public ResponseEntity<ResponseStructure<Boolean>> checkSpotifyData(){
		return userService.checkSpotifyData();
	}
	
	@DeleteMapping(value = "/deleteUser")
	public ResponseEntity<ResponseStructure<String>> deleteUser(){
		return userService.deleteUser();
	}
}
