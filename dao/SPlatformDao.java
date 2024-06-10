package com.optum.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.optum.entity.SPlatform;

@Repository
public interface SPlatformDao extends JpaRepository<SPlatform, Integer> {

	 @Query("SELECT p FROM SPlatform p WHERE p.sPlatformName = :platformName")
    SPlatform findBySPlatformName(String platformName);

}
