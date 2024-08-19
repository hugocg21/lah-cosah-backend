package com.lahcosah.backend.model;

import jakarta.persistence.*;

@Entity
public class Media {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;
	private String url;

	@Column(nullable = true)
	private String folder;

	public Media() {
	}

	public Media(String name, String url, String folder) {
		this.name = name;
		this.url = url;
		this.folder = folder;
	}

	// Getters and Setters
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getFolder() {
		return folder;
	}

	public void setFolder(String folder) {
		this.folder = folder;
	}
}
