package com.optum.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.optum.entity.GppFieldDetails;
import com.optum.entity.SotFieldDetails;
import com.optum.entity.SotGppRenameFieldsMapping;

@Repository
public interface SotGppRenameFieldsMappingDao extends JpaRepository<SotGppRenameFieldsMapping, Integer> {
	List<SotGppRenameFieldsMapping> findBySotFieldDetails_SotFieldRenameContainingOrGppFieldDetails_GppFieldRenameContaining(
			String sotFieldRename, String gppFieldRename);

	@Query("SELECT m FROM SotGppRenameFieldsMapping m WHERE m.sotFieldDetails = :sotFieldDetails AND m.gppFieldDetails = :gppFieldDetails")
	SotGppRenameFieldsMapping findBySotFieldDetailsAndGppFieldDetails(
			@Param("sotFieldDetails") SotFieldDetails sotFieldDetails,
			@Param("gppFieldDetails") GppFieldDetails gppFieldDetails);

	@Query("SELECT m FROM SotGppRenameFieldsMapping m WHERE m.sotFieldDetails = :sotFieldDetails")
	SotGppRenameFieldsMapping findBySotFieldDetails(@Param("sotFieldDetails") SotFieldDetails sotFieldDetails);

	@Query("SELECT m FROM SotGppRenameFieldsMapping m WHERE m.gppFieldDetails = :gppFieldDetails")
	SotGppRenameFieldsMapping findByGppFieldDetails(@Param("gppFieldDetails") GppFieldDetails gppFieldDetails);
	
	  @Query("SELECT m FROM SotGppRenameFieldsMapping m WHERE (:sotRename IS NULL OR :sotRename = '' OR m.sotFieldDetails.sotFieldRename = :sotRename) AND (:gppRename IS NULL OR :gppRename = '' OR m.gppFieldDetails.gppFieldRename = :gppRename)")
	    List<SotGppRenameFieldsMapping> searchBySotAndGppRename(@Param("sotRename") String sotRename, @Param("gppRename") String gppRename);
	  
	  Optional<SotGppRenameFieldsMapping> findBySotGppRid(int sotGppRid);
}
