package com.dormnet.sportservice.repository;

import com.dormnet.sportservice.model.SportEvent;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SportRepository extends MongoRepository<SportEvent, String> {
}
