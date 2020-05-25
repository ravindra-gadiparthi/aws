package org.cloudcafe.aws.chapter3.services;


import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.transfer.TransferManager;
import org.cloudcafe.aws.chapter3.model.Const;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class S3Service {

    private TransferManager transferManager;

    private ResourceLoader resourceLoader;

    private AmazonS3 amazonS3;

    public S3Service(TransferManager transferManager, ResourceLoader resourceLoader, AmazonS3 amazonS3) {
        this.transferManager = transferManager;
        this.resourceLoader = resourceLoader;
        this.amazonS3 = amazonS3;
    }

    private static String UPLOAD_ROOT = "UPLOAD_DIR";

    @Value("${bucketName}")
    private String bucketName;

    public void uploadFile(MultipartFile file, String username) throws IOException {

        Path copyLocation = Paths.get(UPLOAD_ROOT + File.separator + StringUtils.cleanPath(file.getOriginalFilename()));
        Files.copy(file.getInputStream(), copyLocation, StandardCopyOption.REPLACE_EXISTING);
        PutObjectRequest objectRequest = new PutObjectRequest(bucketName,
                String.format(Const.S3UploadLink.getValue(), username, file.getOriginalFilename()),
                copyLocation.toFile());
        transferManager.upload(objectRequest);
    }

    public Resource loadResource(String username, String fileName) {
        return resourceLoader.getResource(String.format(Const.S3Resource.getValue(), bucketName, username, fileName));
    }

    public void deleteResource(String username, String fileName) {
        amazonS3.deleteObject(bucketName, String.format(Const.S3UploadLink.getValue(), username, fileName));
    }

}
