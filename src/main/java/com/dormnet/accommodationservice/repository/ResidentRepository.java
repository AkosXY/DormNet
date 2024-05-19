package com.dormnet.accommodationservice.repository;

import com.dormnet.accommodationservice.modell.Resident;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResidentRepository extends JpaRepository<Resident, Long> {
}
