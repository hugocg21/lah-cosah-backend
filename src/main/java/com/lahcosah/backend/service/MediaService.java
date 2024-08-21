package com.lahcosah.backend.service;

import com.lahcosah.backend.model.Media;
import com.lahcosah.backend.repository.MediaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class MediaService {

    @Autowired
    private MediaRepository mediaRepository;

    @Autowired
    private FirebaseStorageService firebaseStorageService;

    public Media saveMedia(MultipartFile file, String folder) throws IOException {
        String fileUrl = firebaseStorageService.uploadFile(file, folder);
        Media media = new Media(file.getOriginalFilename(), fileUrl, folder);
        return mediaRepository.save(media);
    }

    public List<String> saveMedias(MultipartFile[] files, String folder) throws IOException {
        List<String> urls = new ArrayList<>();
        for (MultipartFile file : files) {
            urls.add(firebaseStorageService.uploadFile(file, folder));
        }
        return urls;
    }

    public boolean deleteMedia(Long id) throws IOException {
        Optional<Media> mediaOptional = mediaRepository.findById(id);
        if (mediaOptional.isPresent()) {
            Media media = mediaOptional.get();
            firebaseStorageService.deleteFile(media.getUrl());
            mediaRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public void moveMediaToFolder(Long mediaId, String newFolder) throws IOException {
        Optional<Media> mediaOptional = mediaRepository.findById(mediaId);
        if (mediaOptional.isPresent()) {
            Media media = mediaOptional.get();
            String newUrl = firebaseStorageService.moveFile(media.getUrl(), newFolder);
            media.setUrl(newUrl);
            media.setFolder(newFolder);
            mediaRepository.save(media);
        }
    }
}
