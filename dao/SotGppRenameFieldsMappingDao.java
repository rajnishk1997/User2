package com.optum.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.optum.entity.SotGppRenameFieldsMapping;

@Repository
public interface SotGppRenameFieldsMappingDao extends JpaRepository<SotGppRenameFieldsMapping, Integer> {
    List<SotGppRenameFieldsMapping> findBySotFieldDetails_SotFieldRenameContainingOrGppFieldDetails_GppFieldRenameContaining(String sotFieldRename, String gppFieldRename);
}
