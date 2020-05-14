package org.cloudcafe.aws.rekognition.services;

import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.AmazonRekognitionClientBuilder;
import com.amazonaws.services.rekognition.model.DetectLabelsRequest;
import com.amazonaws.services.rekognition.model.DetectLabelsResult;
import com.amazonaws.services.rekognition.model.Label;
import com.amazonaws.services.rekognition.model.S3Object;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import lombok.extern.slf4j.Slf4j;
import org.cloudcafe.aws.rekognition.model.Image;
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
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class ImageService {

    private static String UPLOAD_ROOT = "UPLOAD_DIR";

    private AmazonS3Client amazonS3Client;

    AmazonRekognition rekognitionClient = AmazonRekognitionClientBuilder.defaultClient();


    @Value("${bucketName}")
    private String bucketName;

    public ImageService(AmazonS3Client amazonS3Client) {
        this.amazonS3Client = amazonS3Client;
    }


    public Flux<Image> findAllImages() {
        try {
            return Flux.fromIterable(
                    amazonS3Client
                            .listObjects(bucketName)
                            .getObjectSummaries()
            ).map(objectSummary ->
                    new Image(objectSummary.getBucketName(),
                            objectSummary.getKey(),
                            getImageLabels(objectSummary)));
        } catch (Exception e) {
            e.printStackTrace();
            return Flux.empty();
        }
    }

    private List<Label> getImageLabels(S3ObjectSummary objectSummary) {
        DetectLabelsRequest labelsRequest = new DetectLabelsRequest();
        com.amazonaws.services.rekognition.model.Image image = new com.amazonaws.services.rekognition.model.Image();
        image.setS3Object(new S3Object().withBucket(objectSummary.getBucketName()).withName(objectSummary.getKey()));
        labelsRequest.setImage(image);
        DetectLabelsResult detectLabels = rekognitionClient.detectLabels(labelsRequest);

        return detectLabels.getLabels().subList(0,3);
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
