package com.optum.dao;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.optum.entity.ProjectSOT;

public interface ProjectSOTDao  extends JpaRepository<ProjectSOT, Integer> {

	   @Modifying
	    @Transactional
	    @Query(value = "UPDATE rx_project_sot SET validated_json = :validatedJson WHERE uid = :uid", nativeQuery = true)
	    void updateValidationJson(String validatedJson, long uid);
}
