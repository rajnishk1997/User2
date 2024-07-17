package com.optum.dao;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.optum.entity.ProjectSOT;

public interface ProjectSOTDao  extends JpaRepository<ProjectSOT, Integer> {

	   @Modifying
	    @Transactional
	    @Query(value = "UPDATE rx_project_sot SET validated_json = :validatedJson WHERE uid = :uid", nativeQuery = true)
	    void updateValidationJson(String validatedJson, long uid);

	 @Query("SELECT p.validatedJson4 FROM ProjectSOTEntity p WHERE p.sotGppIdMapping = :sotGppIdMapping AND p.uid = :uid")
    String findValidatedJson4BySotGppIdMapping(@Param("uid") String uid, @Param("sotGppIdMapping") int sotGppIdMapping);

    // Method to fetch validated_json28 based on sotgppid_mapping and uid
    @Query("SELECT p.validatedJson28 FROM ProjectSOTEntity p WHERE p.sotGppIdMapping = :sotGppIdMapping AND p.uid = :uid")
    String findValidatedJson28BySotGppIdMapping(@Param("uid") String uid, @Param("sotGppIdMapping") int sotGppIdMapping);

    // Method to fetch validated_json based on sotgppid_mapping and uid
    @Query("SELECT p.validatedJson FROM ProjectSOTEntity p WHERE p.sotGppIdMapping = :sotGppIdMapping AND p.uid = :uid")
    String findValidatedJsonBySotGppIdMapping(@Param("uid") String uid, @Param("sotGppIdMapping") int sotGppIdMapping);
}
