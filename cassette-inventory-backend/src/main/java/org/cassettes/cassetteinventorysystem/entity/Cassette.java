package org.cassettes.cassetteinventorysystem.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Cassette {
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	@JsonIgnore
	private User user; 
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@Column(nullable = false)
	private String title;
	
	@Column(nullable = false)
	private String name;
	
	@Column(nullable = false)
	private long year;
	
	@Column(nullable = false)
	private String format;
	
	@Column(nullable = false)
	private String cover_image;
	
	@Column(nullable = false)
	private List<String> genre;
	
	@Column(nullable = false)
	private List<String> style;
	
	@Column(nullable = false)
	private LocalDate date;
	
	@Column(nullable = true)
	private String album_uri;
	
	@Column(nullable = false)
	private String resource_url;
	
	@Column(nullable = true)
	private int track_list_size;
	
	@ElementCollection
	@CollectionTable(name = "cassette_tracks", joinColumns = @JoinColumn(name = "cassette_id"))
	@Column(name = "track", nullable = false)
	private List<String> track_list = new ArrayList<>();

	public Cassette(long id, String title, String name, long year, String format, String cover_image,
			List<String> genre, List<String> style, LocalDate date, List<String> track_list,
			String album_uri, String resource_url, int track_list_size) {
		super();
		this.id = id;
		this.title = title;
		this.name = name;
		this.year = year;
		this.format = format;
		this.cover_image = cover_image;
		this.genre = genre;
		this.style = style;
		this.date = date;
		this.track_list = track_list;
		this.album_uri = album_uri;
		this.resource_url = resource_url;
		this.track_list_size = track_list_size;
	}

	public Cassette() {
		super();
	}
	
	public User getUser() {
		return user;
	}
	
	public void setUser(User user) {
		this.user = user;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public long getYear() {
		return year;
	}

	public void setYear(long year) {
		this.year = year;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public String getCover_image() {
		return cover_image;
	}

	public void setCover_image(String cover_image) {
		this.cover_image = cover_image;
	}

	public List<String> getGenre() {
		return genre;
	}

	public void setGenre(List<String> genre) {
		this.genre = genre;
	}

	public List<String> getStyle() {
		return style;
	}

	public void setStyle(List<String> style) {
		this.style = style;
	}
	
	public LocalDate getDate() {
		return date;
	}
	
	public void setDate(LocalDate date) {
		this.date = date;
	}
	
	public List<String> getTrack_list(){
		return track_list;
	}
	
	public void setTrack_list(List<String> track_list){
		this.track_list = track_list;
	}
	
	public String getAlbum_uri() {
		return album_uri;
	}
	
	public void setAlbum_uri(String albumUri) {
		this.album_uri = albumUri;
	}
	
	public String getResource_url() {
		return resource_url;
	}
	
	public void setResource_url(String resourceUrl) {
		this.resource_url = resourceUrl;
	}
	
	public int getTrack_list_size() {
		return track_list_size;
	}
	
	public void setTrack_list_size(int trackListSize) {
		this.track_list_size = trackListSize;
	}

}
