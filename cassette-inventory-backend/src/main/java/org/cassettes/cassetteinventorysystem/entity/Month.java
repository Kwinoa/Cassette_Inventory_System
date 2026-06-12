package org.cassettes.cassetteinventorysystem.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class Month {
	@Column(name = "month", nullable = false)
	private int month;

	@Column(name = "cassette_count", nullable = false)
	private int cassette_count;

	public Month() {
	}
	
	public Month(int month, int data) {
		this.month = month;
		this.cassette_count = data;
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public int getCassette_count() {
		return cassette_count;
	}

	public void setCassette_count(int cassette_count) {
		this.cassette_count = cassette_count;
	}
}
