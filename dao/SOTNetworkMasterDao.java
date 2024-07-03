package com.optum.dao;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.optum.entity.SOTNetworkMaster;
import com.optum.entity.SotGppNetworkFieldMapping;
@Repository
public interface SOTNetworkMasterDao  extends JpaRepository<SOTNetworkMaster, Integer> {
	 @Query("SELECT s FROM SOTNetworkMaster s WHERE " +
	           "(:sotNetworkName IS NULL OR s.sotNetworkName = :sotNetworkName) AND " +
	           "(:gppNetworkName IS NULL OR s.gppNetworkName = :gppNetworkName) AND " +
	           "(:platformName IS NULL OR s.platform.platformName = :platformName)")
	    List<SOTNetworkMaster> searchByCriteria(@Param("sotNetworkName") String sotNetworkName,
	                                            @Param("gppNetworkName") String gppNetworkName,
	                                            @Param("platformName") String platformName);
	 
	 Optional<SOTNetworkMaster> findById(int sRid);
   
	  @Query("SELECT s FROM SOTNetworkMaster s WHERE " +
	           "(:keyword IS NULL OR s.sotNetworkName LIKE %:keyword% OR " +
	           "s.gppNetworkName LIKE %:keyword% OR s.platform.platformName LIKE %:keyword%)")
	    List<SOTNetworkMaster> searchByKeyword(@Param("keyword") String keyword);

	SotGppNetworkFieldMapping findBySotFieldDetails_SotFieldRename(String sotNetwork);

	SOTNetworkMaster findBySotNetworkName(String sotNetwork);
}