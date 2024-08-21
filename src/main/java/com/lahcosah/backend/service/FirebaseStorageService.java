package com.lahcosah.backend.service;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.firebase.cloud.StorageClient;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
public class FirebaseStorageService {

    public String uploadFile(MultipartFile file, String folder) throws IOException {
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        String filePath = (folder != null && !folder.isEmpty()) ? folder + "/" + fileName : fileName;

        Bucket bucket = StorageClient.getInstance().bucket();
        Blob blob = bucket.create(filePath, file.getInputStream(), file.getContentType());

        return blob.getMediaLink(); // Devuelve la URL del archivo subido
    }

    public boolean deleteFile(String fileUrl) {
        Bucket bucket = StorageClient.getInstance().bucket();
        return bucket.get(fileUrl).delete();
    }

    public String moveFile(String oldUrl, String newFolder) throws IOException {
        Bucket bucket = StorageClient.getInstance().bucket();
        Blob blob = bucket.get(oldUrl);
        
        if (blob != null) {
            String newFileName = (newFolder != null && !newFolder.isEmpty()) ? newFolder + "/" + blob.getName() : blob.getName();
            Blob newBlob = bucket.create(newFileName, blob.getContent(), blob.getContentType());
            blob.delete();
            return newBlob.getMediaLink();
        }
        
        throw new IOException("Failed to move the file.");
    }
}
