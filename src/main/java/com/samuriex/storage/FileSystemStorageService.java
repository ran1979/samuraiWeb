package com.samuriex.storage;

import com.samuriex.analyze.SamuraiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

@Service
public class FileSystemStorageService implements StorageService {

    private final Path rootLocation;

    @Autowired
    SamuraiService fileService;


    @Autowired
    public FileSystemStorageService(StorageProperties properties) {
        //init of files location on creations
        this.rootLocation = Paths.get(properties.getLocation());
    }

    @Override
    public Path store(MultipartFile file) {
        try {
            if (file.isEmpty()) {
                throw new StorageException("Failed to store empty analyze " + file.getOriginalFilename());
            }
            Path pathToNewFile = this.rootLocation.resolve(file.getOriginalFilename());
            Files.copy(file.getInputStream(), pathToNewFile);
            return pathToNewFile;
        } catch (IOException e) {
            throw new StorageException("Failed to store analyze " + file.getOriginalFilename(), e);
        }
    }

    @Override
    public void storeAndAnalyze(MultipartFile file) {
        Path pathToNewFile = store(file);
        try {
            fileService.examine(pathToNewFile.toFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Stream<Path> loadAll() {
        try {
            return Files.walk(this.rootLocation, 1)
                    .filter(path -> !path.equals(this.rootLocation))
                    .map(path -> this.rootLocation.relativize(path));
        } catch (IOException e) {
            throw new StorageException("Failed to read stored files", e);
        }

    }

    @Override
    public Path load(String filename) {
        return rootLocation.resolve(filename);
    }

    @Override
    public Resource loadAsResource(String filename) {
        try {
            Path file = load(filename);
            Resource resource = new UrlResource(file.toUri());
            if(resource.exists() || resource.isReadable()) {
                return resource;
            }
            else {
                throw new StorageFileNotFoundException("Could not read analyze: " + filename);

            }
        } catch (MalformedURLException e) {
            throw new StorageFileNotFoundException("Could not read analyze: " + filename, e);
        }
    }

    @Override
    public void deleteAll() {
        FileSystemUtils.deleteRecursively(rootLocation.toFile());
    }

    @Override
    public void init() {
        try {
            Files.createDirectory(rootLocation);
        } catch (IOException e) {
            throw new StorageException("Could not initialize storage", e);
        }
    }
}
