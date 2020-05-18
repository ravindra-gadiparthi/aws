package org.cloudcafe.aws.chapter3.services;

import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.AmazonRekognitionClientBuilder;
import com.amazonaws.services.rekognition.model.DetectLabelsRequest;
import com.amazonaws.services.rekognition.model.DetectLabelsResult;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import lombok.extern.slf4j.Slf4j;
import org.cloudcafe.aws.chapter3.model.Image;
import org.cloudcafe.aws.chapter3.repository.ImageRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service
@Slf4j
public class ImageService {

    private static String UPLOAD_ROOT = "UPLOAD_DIR";

    private AmazonS3Client amazonS3Client;

    private ImageRepository imageRepository;

    AmazonRekognition rekognitionClient = AmazonRekognitionClientBuilder.defaultClient();


    @Value("${bucketName}")
    private String bucketName;

    public ImageService(AmazonS3Client amazonS3Client, ImageRepository imageRepository) {
        this.amazonS3Client = amazonS3Client;
        this.imageRepository = imageRepository;
    }


    public Flux<Image> findAllImages() {
        return Flux.fromIterable(imageRepository.findAll());
    }

    private Mono<String> getImageLabels(File photo) {

        return Mono.fromCallable(() -> {
            ByteBuffer imageBytes;
            try (InputStream inputStream = new FileInputStream(photo)) {
                imageBytes = ByteBuffer.wrap(IOUtils.toByteArray(inputStream));
            }

            DetectLabelsRequest labelsRequest = new DetectLabelsRequest();
            com.amazonaws.services.rekognition.model.Image image = new com.amazonaws.services.rekognition.model.Image();
            image.withBytes(imageBytes);
            labelsRequest.setImage(image);
            DetectLabelsResult detectLabels = rekognitionClient.detectLabels(labelsRequest);
            return detectLabels.getLabels().subList(0,5).toString();

        });

    }

    public Mono<S3ObjectInputStream> findOneImage(String imageName) {
        return Mono.fromSupplier(() -> amazonS3Client.getObject(bucketName, imageName).getObjectContent());
    }

    public Mono<Void> createImage(Flux<FilePart> files) {

        return files.flatMap(filePart ->
                filePart.transferTo(Paths.get(UPLOAD_ROOT, filePart.filename()))
                        .then(Mono.fromCallable(() -> {
                            return Paths.get(UPLOAD_ROOT, filePart.filename()).toFile();
                        }))
                        .flatMap(file -> Mono.zip(
                                Mono.fromCallable(() -> amazonS3Client.putObject(bucketName, file.getName(), file)),
                                getImageLabels(file),
                                (putObjectResult, labels) -> {
                                    return imageRepository.save(Image.builder().labels(labels)
                                            .bucketName(bucketName)
                                            .name(file.getName()).build());
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
