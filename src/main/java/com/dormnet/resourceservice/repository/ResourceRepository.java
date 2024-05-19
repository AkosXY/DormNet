package com.dormnet.resourceservice.repository;

import com.dormnet.resourceservice.model.Resource;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResourceRepository extends JpaRepository<Resource, Long> {
}
