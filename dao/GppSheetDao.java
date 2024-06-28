package com.optum.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.optum.entity.GppSheet;

@Repository
public interface GppSheetDao extends JpaRepository<GppSheet, Long> {

    GppSheet findByGppSheetName(String gppSheetName);
}
