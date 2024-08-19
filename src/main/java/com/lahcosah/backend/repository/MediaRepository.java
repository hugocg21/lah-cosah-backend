package com.lahcosah.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.lahcosah.backend.model.Media;

import java.util.List;

public interface MediaRepository extends JpaRepository<Media, Long> {
	List<Media> findByFolder(String folder);
	List<Media> findByFolderIsNull();
}
