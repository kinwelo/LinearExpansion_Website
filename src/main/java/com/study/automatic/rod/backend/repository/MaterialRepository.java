package com.study.automatic.rod.backend.repository;

import com.study.automatic.rod.backend.entity.Material;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MaterialRepository extends JpaRepository<Material, Long> {

    @Query("select m from Material m " +
            "where lower(m.name) like lower(concat('%', :searchTerm, '%'))")
    List<Material> search(@Param("searchTerm") String searchTerm); //

}
