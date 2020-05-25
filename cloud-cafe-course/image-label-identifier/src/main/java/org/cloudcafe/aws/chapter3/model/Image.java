package org.cloudcafe.aws.chapter3.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Image {

    private Integer id;
    private String name;
    private String bucketName;
    private List<String> details;
}
