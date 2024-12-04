package com.dongwoo.SQM.common.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class FileStorageService {
    @Value("${Upload.path.attach}")
    private String uploadPath;

    public Path getUploadDirectory() {
        return Paths.get(uploadPath); // Converts the string path to a Path object
    }
}
