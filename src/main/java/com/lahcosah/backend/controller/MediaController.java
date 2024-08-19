package com.lahcosah.backend.controller;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.lahcosah.backend.model.Media;
import com.lahcosah.backend.repository.MediaRepository;
import com.lahcosah.backend.service.MediaService;

@RestController
@RequestMapping("/api/media")
public class MediaController {

	@Autowired
	private MediaService mediaService;

	@Autowired
	private MediaRepository mediaRepository;

	@SuppressWarnings("null")
	@PostMapping("/upload")
	public ResponseEntity<Media> uploadMedia(@RequestParam("file") MultipartFile file, @RequestParam(required = false) String folder) {
		try {
			Media media = mediaService.saveMedia(file, folder);
			return new ResponseEntity<>(media, HttpStatus.CREATED);
		} catch (IOException e) {
			e.printStackTrace();
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@SuppressWarnings("null")
	@PostMapping("/upload/multiple")
	public ResponseEntity<List<Media>> uploadMedias(@RequestParam("files") MultipartFile[] files, @RequestParam(required = false) String folder) {
		try {
			List<Media> medias = mediaService.saveMedias(files, folder);
			return new ResponseEntity<>(medias, HttpStatus.CREATED);
		} catch (IOException e) {
			e.printStackTrace();
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping
	public List<Media> getAllMedias(@RequestParam(required = false) String folder) {
		return mediaService.getAllMedias(folder);
	}

	@GetMapping("/folders")
	public List<String> getFolders() {
		return mediaService.getFolders();
	}

	@PostMapping("/create-folder")
	public ResponseEntity<Void> createFolder(@RequestBody String folderName) {
		try {
			mediaService.createFolder(folderName);
			return new ResponseEntity<>(HttpStatus.CREATED);
		} catch (RuntimeException e) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping("/{id}")
	public ResponseEntity<Media> getMediaById(@PathVariable Long id) {
		Optional<Media> media = mediaService.getMedia(id);
		return media.map(value -> new ResponseEntity<>(value, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	@PutMapping("/{id}")
	public ResponseEntity<Media> updateMediaName(@PathVariable Long id, @RequestBody Media updatedMedia) {
		Optional<Media> mediaData = mediaRepository.findById(id);

		if (mediaData.isPresent()) {
			Media media = mediaData.get();
			media.setName(updatedMedia.getName());
			mediaRepository.save(media);
			return new ResponseEntity<>(media, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	@GetMapping("/serve")
	@ResponseBody
	public ResponseEntity<Resource> serveFile(@RequestParam("filename") String filename) throws IOException {
		try {
			Path file = Paths.get("C:/ruta/a/uploads").resolve(filename);
			Resource resource = new UrlResource(file.toUri());

			if (resource.exists() || resource.isReadable()) {
				String contentType = Files.probeContentType(file);
				if (contentType == null) {
					contentType = "application/octet-stream";
				}
				return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType)).header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"").body(resource);
			} else {
				return ResponseEntity.notFound().build();
			}
		} catch (MalformedURLException e) {
			return ResponseEntity.badRequest().build();
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteMedia(@PathVariable Long id) {
		try {
			boolean deleted = mediaService.deleteMedia(id);
			if (deleted) {
				return new ResponseEntity<>(HttpStatus.NO_CONTENT);
			} else {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
		} catch (IOException e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping("/delete-multiple")
	public ResponseEntity<Void> deleteMultipleMedias(@RequestBody List<Long> ids) throws IOException {
		for (Long id : ids) {
			mediaService.deleteMedia(id);
		}
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@DeleteMapping("/delete-folder")
	public ResponseEntity<Void> deleteFolder(@RequestParam String folderName) {
		try {
			boolean deleted = mediaService.deleteFolder(folderName);
			if (deleted) {
				return new ResponseEntity<>(HttpStatus.NO_CONTENT);
			} else {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
		} catch (IOException e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (RuntimeException e) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

	@PutMapping("/{id}/move")
	public ResponseEntity<Void> moveMediaToFolder(@PathVariable Long id, @RequestBody Map<String, String> request) {
		String newFolder = request.get("folder");
		try {
			mediaService.moveMediaToFolder(id, newFolder);
			return ResponseEntity.ok().build();
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@GetMapping("/api/media")
	public ResponseEntity<?> getMedia() {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Access-Control-Allow-Origin", "https://lahcosahnuehtrah.netlify.app");
		headers.add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
		headers.add("Access-Control-Allow-Headers", "Authorization, Content-Type");
		return ResponseEntity.ok().headers(headers).body("Media data");
	}
}
