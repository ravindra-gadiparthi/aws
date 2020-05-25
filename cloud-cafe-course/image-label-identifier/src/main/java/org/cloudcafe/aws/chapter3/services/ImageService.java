package org.cloudcafe.aws.chapter3.services;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import lombok.extern.slf4j.Slf4j;
import org.cloudcafe.aws.chapter3.model.Image;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ImageService {
    private static String UPLOAD_ROOT = "UPLOAD_DIR";

    private AmazonS3Client amazonS3Client;

    private RecognitionService recognitionService;

    @Value("${bucketName}")
    private String bucketName;

    public ImageService(AmazonS3Client amazonS3Client, RecognitionService recognitionService) {
        this.recognitionService = recognitionService;
        this.amazonS3Client = amazonS3Client;
    }


    public List<Image> findAll() {
        return amazonS3Client.listObjects(bucketName).getObjectSummaries()
                .stream().map(objectSummary -> Image.builder()
                        .details(recognitionService.getDetails(objectSummary.getBucketName(), objectSummary.getKey()))
                        .bucketName(objectSummary.getBucketName()).name(objectSummary.getKey())
                        .id(objectSummary.getKey().hashCode()).build()
                ).collect(Collectors.toList());
    }

    public S3ObjectInputStream findOneImage(String fileName) {
        S3Object s3Object = amazonS3Client.getObject(bucketName, fileName);
        return s3Object.getObjectContent();
    }

    public void createImage(MultipartFile file) {
        try {
            Path copyLocation = Paths.get(UPLOAD_ROOT + File.separator + StringUtils.cleanPath(file.getOriginalFilename()));
            Files.copy(file.getInputStream(), copyLocation, StandardCopyOption.REPLACE_EXISTING);
            amazonS3Client.putObject(bucketName, file.getOriginalFilename(), copyLocation.toFile());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Could not store file " + file.getOriginalFilename()
                    + ". Please try again!");
        }

    }


    public void deleteImage(String fileName) {
        amazonS3Client.deleteObject(bucketName, fileName);
    }

    @Bean
    private CommandLineRunner init() {
        return (args) -> {
            FileSystemUtils.deleteRecursively(new File(UPLOAD_ROOT));

            Files.createDirectories(Paths.get(UPLOAD_ROOT));
        };
    }
}
