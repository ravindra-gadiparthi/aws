package org.cloudcafe.aws.chapter6.repository;

import com.amazonaws.xray.spring.aop.XRayEnabled;
import org.cloudcafe.aws.chapter6.model.Image;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@XRayEnabled
public interface ImageRepository extends PagingAndSortingRepository<Image, Integer> {


    Image findByName(String name);

    List<Image> findByUsername(String username);

    void deleteImageByNameAndUsername(String fileName, String username);
}
