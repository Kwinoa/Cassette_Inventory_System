package org.cassettes.cassetteinventorysystem.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SecurityConfig implements WebMvcConfigurer {
	
	@Bean
	public PasswordEncoder passwordEncoder() {
	    return new BCryptPasswordEncoder();
	}
	
	@Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
	
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
	    http
	        .cors(cors -> cors.configurationSource(request -> {
	            CorsConfiguration config = new CorsConfiguration();
	            config.setAllowedOrigins(List.of("http://127.0.0.1:3000"));
	            config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
	            config.setAllowedHeaders(List.of("*")); // Added for completeness
	            config.setAllowCredentials(true);
	            return config;
	        }))
	        .csrf(csrf -> csrf.disable())
	        // Important: checks http session for security context
	        .securityContext((context) -> context
	            .securityContextRepository(new HttpSessionSecurityContextRepository())
	        )
	        .sessionManagement(session ->
	            session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
	            .sessionConcurrency(concurrency -> concurrency.maximumSessions(3))
	            .invalidSessionUrl("/logout"))
	        .authorizeHttpRequests(auth -> auth
	        	.requestMatchers(org.springframework.http.HttpMethod.OPTIONS, "/**").permitAll()
	            .requestMatchers("/api/login", "/api/logout", "/api/register", "/search", "/public/media/**").permitAll()
	            .anyRequest().authenticated()
	        )
	        .exceptionHandling(ex -> ex
	            .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
	        );
	        
	    return http.build();
	}
	
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("public/media/**").addResourceLocations("file:media/");
	}
}
