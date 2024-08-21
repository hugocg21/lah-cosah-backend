package com.lahcosah.backend.service;

import java.io.IOException;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.firebase.cloud.StorageClient;

@Service
public class FirebaseStorageService {

    public String uploadFile(MultipartFile file, String folder) throws IOException {
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        String filePath = (folder != null && !folder.isEmpty()) ? folder + "/" + fileName : fileName;

        Bucket bucket = StorageClient.getInstance().bucket();
        Blob blob = bucket.create(filePath, file.getInputStream(), file.getContentType());

        return filePath; // Save storage path, not media link
    }

    public boolean deleteFile(String storagePath) {
        Bucket bucket = StorageClient.getInstance().bucket();
        Blob blob = bucket.get(storagePath);
        return blob != null && blob.delete();
    }

    public String moveFile(String oldStoragePath, String newFolder) throws IOException {
        Bucket bucket = StorageClient.getInstance().bucket();
        Blob blob = bucket.get(oldStoragePath);
        
        if (blob != null) {
            String newFileName = (newFolder != null && !newFolder.isEmpty()) ? newFolder + "/" + blob.getName() : blob.getName();
            Blob newBlob = bucket.create(newFileName, blob.getContent(), blob.getContentType());
            blob.delete();
            return newFileName; // Return new storage path
        }
        
        throw new IOException("Failed to move the file.");
    }
}

