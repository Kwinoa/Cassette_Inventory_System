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
	private User user; 
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@Column(nullable = false)
	private String title;
	
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
	
	@ElementCollection
	@CollectionTable(name = "cassette_tracks", joinColumns = @JoinColumn(name = "cassette_id"))
	@Column(name = "track", nullable = false)
	private List<String> track_list = new ArrayList<>();

	public Cassette(long id, String title, long year, String format, String cover_image,
			List<String> genre, List<String> style, LocalDate date, List<String> track_list) {
		super();
		this.id = id;
		this.title = title;
		this.year = year;
		this.format = format;
		this.cover_image = cover_image;
		this.genre = genre;
		this.style = style;
		this.date = date;
		this.track_list = track_list;
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
	
	public List<String> getTrack_List(){
		return track_list;
	}
	
	public void setTrack_List(List<String> track_list){
		this.track_list = track_list;
	}

}
