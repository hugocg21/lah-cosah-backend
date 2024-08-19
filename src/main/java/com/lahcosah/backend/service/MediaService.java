package com.lahcosah.backend.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.lahcosah.backend.model.Media;
import com.lahcosah.backend.repository.MediaRepository;

@Service
public class MediaService {
	@Autowired
	private MediaRepository mediaRepository;

	private final String uploadDir = "C:/ruta/a/uploads/";

	public Media saveMedia(MultipartFile file, String folder) throws IOException {
		String directoryPath = uploadDir;

		if (folder != null && !folder.isEmpty()) {
			directoryPath += folder + "/";
		}

		File directory = new File(directoryPath);

		if (!directory.exists()) {
			directory.mkdirs();
		}

		String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();

		File dest = new File(directoryPath + fileName);
		file.transferTo(dest);

		String fileUrl = folder != null ? "/uploads/" + folder + "/" + fileName : "/uploads/" + fileName;
		Media media = new Media(fileName, fileUrl, folder);

		return mediaRepository.save(media);
	}

	public List<Media> saveMedias(MultipartFile[] files, String folder) throws IOException {
		List<Media> savedMedias = new ArrayList<>();

		for (MultipartFile file : files) {
			savedMedias.add(saveMedia(file, folder));
		}

		return savedMedias;
	}

	public List<Media> getAllMedias(String folder) {
		if (folder == null || folder.isEmpty()) {
			return mediaRepository.findByFolderIsNull();
		} else {
			return mediaRepository.findByFolder(folder);
		}
	}

	public Optional<Media> getMedia(Long id) {
		return mediaRepository.findById(id);
	}

	public boolean deleteMedia(Long id) throws IOException {
		Optional<Media> mediaOptional = mediaRepository.findById(id);

		if (mediaOptional.isPresent()) {
			Media media = mediaOptional.get();
			String directoryPath = media.getFolder() != null ? uploadDir + media.getFolder() + "/" : uploadDir;
			File file = new File(directoryPath + media.getName());

			if (file.exists()) {
				file.delete();
			}

			mediaRepository.deleteById(id);
			return true;
		} else {
			return false;
		}
	}

	public List<String> getFolders() {
		File rootDir = new File(uploadDir);
		File[] directories = rootDir.listFiles(File::isDirectory);

		List<String> folderNames = new ArrayList<>();
		if (directories != null) {
			for (File dir : directories) {
				folderNames.add(dir.getName());
			}
		}
		return folderNames;
	}

	public void createFolder(String folderName) {
		File folder = new File(uploadDir + folderName);
		if (!folder.exists()) {
			boolean created = folder.mkdirs();
			if (!created) {
				throw new RuntimeException("No se pudo crear la carpeta: " + folderName);
			}
		} else {
			throw new RuntimeException("La carpeta ya existe: " + folderName);
		}
	}

	public boolean deleteFolder(String folderName) throws IOException {
		File folder = new File(uploadDir + folderName);

		if (folder.exists() && folder.isDirectory()) {
			File[] files = folder.listFiles();
			if (files != null) {
				for (File file : files) {
					file.delete();
				}
			}
			return folder.delete();
		} else {
			throw new RuntimeException("La carpeta no existe o no es un directorio: " + folderName);
		}
	}

	public void moveMediaToFolder(Long mediaId, String newFolder) throws IOException {
		Optional<Media> mediaOptional = mediaRepository.findById(mediaId);

		if (mediaOptional.isPresent()) {
			Media media = mediaOptional.get();

			String oldFolderPath = media.getFolder() != null ? uploadDir + media.getFolder() + "/" : uploadDir;
			String newFolderPath = (newFolder != null && !newFolder.isEmpty()) ? uploadDir + newFolder + "/" : uploadDir;

			File newDir = new File(newFolderPath);
			if (!newDir.exists()) {
				newDir.mkdirs();
			}

			File oldFile = new File(oldFolderPath + media.getName());
			File newFile = new File(newFolderPath + media.getName());

			if (oldFile.renameTo(newFile)) {
				media.setFolder(newFolder != null && !newFolder.isEmpty() ? newFolder : null);
				mediaRepository.save(media);
			} else {
				throw new IOException("Failed to move the file to the new folder.");
			}
		} else {
			throw new IOException("Media not found.");
		}
	}
}
