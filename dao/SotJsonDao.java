package com.optum.dao;

import com.optum.entity.SotJsonEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SotJsonDao extends JpaRepository<SotJsonEntity, Integer> {
    
   // Custom query to fetch JSON data by ID
//    @Query("SELECT s FROM SotJsonEntity s WHERE s.id = :id")
//    SotJsonEntity findById(int id);
}

