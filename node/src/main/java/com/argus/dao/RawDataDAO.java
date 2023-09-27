package com.argus.dao;

import com.argus.entity.RawData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RawDataDAO extends JpaRepository<RawData, Long> {
}
