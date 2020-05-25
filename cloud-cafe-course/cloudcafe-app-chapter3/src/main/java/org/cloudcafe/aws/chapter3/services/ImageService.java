package org.cloudcafe.aws.chapter3.services;

import lombok.extern.slf4j.Slf4j;
import org.cloudcafe.aws.chapter3.model.Const;
import org.cloudcafe.aws.chapter3.model.Image;
import org.cloudcafe.aws.chapter3.repository.ImageRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class ImageService {
    private static String UPLOAD_ROOT = "UPLOAD_DIR";

    private S3Service s3Service;

    private RecognitionService recognitionService;

    private ImageRepository repository;

    @Value("${bucketName}")
    private String bucketName;

    public ImageService(S3Service s3Service, RecognitionService recognitionService, ImageRepository repository) {
        this.recognitionService = recognitionService;
        this.repository = repository;
        this.s3Service = s3Service;
    }

    public List<Image> findAll() {
        return repository.findAll();
    }

    public Resource findOneImage(String fileName) {
        return s3Service.loadResource(Const.ROOT.getValue(), fileName);
    }

    @Transactional
    public void createImage(MultipartFile file) {
        try {
            s3Service.uploadFile(file, Const.ROOT.getValue());
            List<String> labels = recognitionService.getDetails(bucketName, file.getOriginalFilename());
            repository.save(Image.builder().bucketName(bucketName).name(file.getOriginalFilename()).labels(Objects.toString(labels))
                    .username(Const.ROOT.getValue()).build());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Could not store file " + file.getOriginalFilename()
                    + ". Please try again!");
        }

    }


    @Transactional
    public void deleteImage(String fileName) {
        s3Service.deleteResource(bucketName, fileName);
        repository.deleteImageByName(fileName);
    }

    @Bean
    private CommandLineRunner init() {
        return (args) -> {
            FileSystemUtils.deleteRecursively(new File(UPLOAD_ROOT));

            Files.createDirectories(Paths.get(UPLOAD_ROOT));
        };
    }
}
