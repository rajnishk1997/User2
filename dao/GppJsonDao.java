package com.optum.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.optum.entity.GppJsonEntity;

@Repository
public interface GppJsonDao extends JpaRepository<GppJsonEntity, Integer> {
    
   // Custom query to fetch JSON data by ID
//    @Query("SELECT s FROM SotJsonEntity s WHERE s.id = :id")
//    SotJsonEntity findById(int id);
}