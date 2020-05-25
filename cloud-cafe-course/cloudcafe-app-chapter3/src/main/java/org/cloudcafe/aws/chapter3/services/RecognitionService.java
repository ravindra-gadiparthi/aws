package org.cloudcafe.aws.chapter3.services;


import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.AmazonRekognitionClientBuilder;
import com.amazonaws.services.rekognition.model.*;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


@Service
@NoArgsConstructor
@Slf4j
public class RecognitionService {

    AmazonRekognition rekognition = AmazonRekognitionClientBuilder.defaultClient();

    public List<String> getDetails(String bucketName, String fileName) {
        try {
            log.info("processing image " + fileName);
            DetectLabelsRequest facesRequest = new DetectLabelsRequest();
            facesRequest.setImage(new Image().withS3Object(new S3Object().withBucket(bucketName).withName(fileName)));
            DetectLabelsResult labels = rekognition.detectLabels(facesRequest);
            return labels.getLabels().stream().filter(label -> label.getConfidence() > 80.0f)
                    .map(Label::getName).collect(Collectors.toList());
        } catch (AmazonRekognitionException e) {
            return Arrays.asList("Invalid Image");
        }
    }
}
