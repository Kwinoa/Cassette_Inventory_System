package org.cassettes.cassetteinventorysystem.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Cassette {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@Column(nullable = false)
	private String title;
	
	@Column(nullable = false)
	private long year;
	
	@Column(nullable = false)
	private String label;
	
	@Column(nullable = false)
	private String format;
	
	@Column(nullable = false)
	private String thumb;
	
	@Column(nullable = false)
	private String url;

	public Cassette(long id, String title, long year, String label, String format, String thumb, String url) {
		super();
		this.id = id;
		this.title = title;
		this.year = year;
		this.label = label;
		this.format = format;
		this.thumb = thumb;
		this.url = url;
	}
	
	public Cassette() {
		super();
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

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public String getThumb() {
		return thumb;
	}

	public void setThumb(String thumb) {
		this.thumb = thumb;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}
