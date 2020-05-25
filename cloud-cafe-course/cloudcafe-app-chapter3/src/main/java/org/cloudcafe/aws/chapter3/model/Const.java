package org.cloudcafe.aws.chapter3.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Const {

    ROOT("root"),
    S3Resource("s3://%s/%s/%s"),
    S3UploadLink("%s/%s");
    private String value;
}
