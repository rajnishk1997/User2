package com.optum.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface GppJson28Dao extends JpaRepository<RxSotGppData, String> {

	 @Query(value = "SELECT gpp_json28 FROM rxcl_audit.rx_sot_gpp_data WHERE uid = :uid", nativeQuery = true)
	    String findGppJson28ByUid(@Param("uid") int uid);

	  @Query(value = "SELECT planc_code_override FROM rxcl_audit.rx_sot_gpp_data WHERE uid = :uid", nativeQuery = true)
    String findPlancCodeOverrideByUid(@Param("uid") int uid);
}

