package com.study.automatic.rod.backend.repository;

import com.study.automatic.rod.backend.entity.Material;
import com.study.automatic.rod.backend.entity.Record;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RecordRepository extends JpaRepository<Record, Long> {
    /*@Query("select r from Record r " +
            "where r.sample_id = :searchLong")
    List<Record> search(@Param("searchLong") Long searchLong); //*/
}
