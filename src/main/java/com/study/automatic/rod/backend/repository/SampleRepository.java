package com.study.automatic.rod.backend.repository;

import com.study.automatic.rod.backend.entity.Material;
import com.study.automatic.rod.backend.entity.Sample;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SampleRepository extends JpaRepository<Sample, Long> {
    @Query("select s from Sample s " +
            "where lower(s.id) like lower(concat('%', :searchTerm, '%'))")
    List<Sample> search(@Param("searchTerm") String searchTerm);
}
