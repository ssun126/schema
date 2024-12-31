package com.dongwoo.SQM.common.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class FileStorageService {
    @Value("${Upload.path.audit}")
    private String uploadPath;

    @Value("${Upload.path.iso}")
    private String uploadPathIso;

    public Path getISOUploadDirectory() {
        return Paths.get(uploadPathIso); // Converts the string path to a Path object
    }

    public Path getUploadDirectory() {
        return Paths.get(uploadPath); // Converts the string path to a Path object
    }
}
