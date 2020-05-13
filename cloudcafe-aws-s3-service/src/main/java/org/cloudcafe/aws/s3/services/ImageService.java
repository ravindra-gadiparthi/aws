package org.cloudcafe.aws.s3.services;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cloudcafe.aws.s3.model.Image;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service
@Slf4j
public class ImageService {

    private static String UPLOAD_ROOT = "UPLOAD_DIR";

    private AmazonS3Client amazonS3Client;


    @Value("${bucketName}")
    private String bucketName;

    public ImageService(AmazonS3Client amazonS3Client){
        this.amazonS3Client = amazonS3Client;
    }


    public Flux<Image> findAllImages() {
        try {
            return Flux.fromIterable(amazonS3Client.listObjects(bucketName).getObjectSummaries())
                    .map(objectSummary -> new Image(objectSummary.getBucketName(), objectSummary.getKey()));
        } catch (Exception e) {
            e.printStackTrace();
            return Flux.empty();
        }
    }

    public Mono<S3ObjectInputStream> findOneImage(String imageName) {
        return Mono.fromSupplier(() -> amazonS3Client.getObject(bucketName, imageName).getObjectContent());
    }

    public Mono<Void> createImage(Flux<FilePart> files) {

        return files.flatMap(filePart ->
                filePart.transferTo(Paths.get(UPLOAD_ROOT, filePart.filename()))
                        .then(Mono.fromCallable(() -> {
                            log.info("uploading to s3 started");
                            File file = Paths.get(UPLOAD_ROOT, filePart.filename()).toFile();
                            return amazonS3Client.putObject(bucketName, filePart.filename(), file);
                        }))
                        .log("uploading to folder completed"))
                .log("creating image")
                .then();
    }

    public Mono<Void> deleteImage(String fileName) {
        return Mono.fromRunnable(() -> {
            try {
                amazonS3Client.deleteObject(bucketName, fileName);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }


    @Bean
    private CommandLineRunner init() {
        return (args) -> {
            FileSystemUtils.deleteRecursively(new File(UPLOAD_ROOT));

            Files.createDirectories(Paths.get(UPLOAD_ROOT));
        };
    }
}
