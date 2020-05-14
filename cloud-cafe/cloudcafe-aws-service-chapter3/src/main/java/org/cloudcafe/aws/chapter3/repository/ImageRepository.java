package org.cloudcafe.aws.chapter3.repository;

import org.cloudcafe.aws.chapter3.model.Image;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageRepository extends PagingAndSortingRepository<Image, Integer> {

    Image findByName(String name);
}
