package org.cloudcafe.aws.rekognition.model;

import com.amazonaws.services.rekognition.model.Label;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Image {

    private String id;
    private String name;
    private List<Label> labels;
}
