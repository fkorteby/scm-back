package com.simple_cabinet_medical.Backend.repository;

import com.simple_cabinet_medical.Backend.model.Ads;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdsRepository extends MongoRepository<Ads,Long> {
}
