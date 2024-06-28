package com.optum.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.optum.dto.SotRenameDto;
import com.optum.entity.SotFieldDetails;

@Repository
public interface SotFieldDetailsDao extends JpaRepository<SotFieldDetails, Integer> {

	  @Query("SELECT s FROM SotFieldDetails s WHERE " +
	           "(LOWER(s.sotFieldName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
	           "OR LOWER(s.sotFieldRename) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
	           "AND (:validation IS NULL OR s.isSOTValidationRequired = :validation)")
	    List<SotFieldDetails> searchByKeywordAndValidation(@Param("keyword") String keyword, @Param("validation") Boolean validation);

	  @Query("SELECT s FROM SotFieldDetails s WHERE s.sotFieldRename = :sotFieldRename")
	    SotFieldDetails findBySotFieldRename(@Param("sotFieldRename") String sotFieldRename);
	  
	  @Query("SELECT new com.optum.dto.SotRenameDto(s.sotRid, s.sotFieldRename) FROM SotFieldDetails s")
	    List<SotRenameDto> findAllSotRenames();
}