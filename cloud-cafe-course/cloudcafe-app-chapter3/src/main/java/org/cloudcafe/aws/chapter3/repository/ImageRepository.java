package org.cloudcafe.aws.chapter3.repository;

import org.cloudcafe.aws.chapter3.model.Image;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImageRepository extends PagingAndSortingRepository<Image, Integer> {


    Image findByName(String name);

    List<Image> findAll();

    List<Image> findByUsername(String username);

    void deleteImageByNameAndUsername(String fileName, String username);

    void deleteImageByName(String fileName);
}

