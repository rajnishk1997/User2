package com.optum.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.optum.entity.GppFieldDetails;

@Repository
public interface GppFieldDetailsDao extends JpaRepository<GppFieldDetails, Integer> {
    @Query("SELECT g FROM GppFieldDetails g WHERE "
           + "(LOWER(g.gppFieldName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR "
           + "LOWER(g.gppFieldRename) LIKE LOWER(CONCAT('%', :keyword, '%'))) "
           + "AND (:validation IS NULL OR g.isGPPValidationRequired = :validation)")
    List<GppFieldDetails> searchByKeywordAndValidation(@Param("keyword") String keyword, @Param("validation") Boolean validation);
}

