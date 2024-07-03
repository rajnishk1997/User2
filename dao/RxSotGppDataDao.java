package com.optum.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
@Repository
public interface RxSotGppDataDao extends JpaRepository<Object, Integer> {

    @Query(value = "SELECT gpp_json4, sot_json FROM rxcl_audit.rx_sot_gpp_data WHERE uid = :uid", nativeQuery = true)
    Object[] findJsonDataByUid(@Param("uid") int uid);
}
