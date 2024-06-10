package com.optum.dao;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.optum.entity.SOTNetworkMaster;
@Repository
public interface SOTNetworkMasterDao  extends JpaRepository<SOTNetworkMaster, Integer> {
	 @Query("SELECT s FROM SOTNetworkMaster s WHERE " +
	           "(:sotNetworkName IS NULL OR s.sSotNetworkName = :sotNetworkName) AND " +
	           "(:gppNetworkName IS NULL OR s.sGppNetworkName = :gppNetworkName) AND " +
	           "(:platformName IS NULL OR s.sPlatform.sPlatformName = :platformName)")
	    List<SOTNetworkMaster> searchByCriteria(@Param("sotNetworkName") String sotNetworkName,
	                                            @Param("gppNetworkName") String gppNetworkName,
	                                            @Param("platformName") String platformName);
	 
	 Optional<SOTNetworkMaster> findBySRid(int sRid);
   
}