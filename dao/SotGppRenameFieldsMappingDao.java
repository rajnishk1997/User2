package com.optum.dao;

import java.util.List;

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

	@Query("SELECT s FROM SotGppRenameFieldsMapping s "
			+ "WHERE (:sotRenameParam IS NULL OR s.sotFieldDetails.sotFieldRename = :sotRenameParam) "
			+ "AND (:gppRenameParam IS NULL OR s.gppFieldDetails.gppFieldRename = :gppRenameParam)")
	List<SotGppRenameFieldsMapping> searchBySotAndGppRename(@Param("sotRenameParam") String sotRenameParam,
			@Param("gppRenameParam") String gppRenameParam);

	@Query("SELECT m FROM SotGppRenameFieldsMapping m WHERE m.sotFieldDetails = :sotFieldDetails")
	SotGppRenameFieldsMapping findBySotFieldDetails(@Param("sotFieldDetails") SotFieldDetails sotFieldDetails);

	@Query("SELECT m FROM SotGppRenameFieldsMapping m WHERE m.gppFieldDetails = :gppFieldDetails")
	SotGppRenameFieldsMapping findByGppFieldDetails(@Param("gppFieldDetails") GppFieldDetails gppFieldDetails);
}