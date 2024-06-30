package com.optum.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.optum.dto.GppSheetDto;
import com.optum.entity.GppSheet;

@Repository
public interface GppSheetDao extends JpaRepository<GppSheet, Long> {

    GppSheet findByGppSheetName(String gppSheetName);
    @Query("SELECT new com.optum.dto.GppSheetDto(g.gppSheetRid, g.gppSheetName, g.isActive) FROM GppSheet g")
    List<GppSheetDto> findAllGppSheetDtos();
}
