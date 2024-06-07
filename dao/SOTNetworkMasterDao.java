package com.optum.dao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.optum.entity.SOTNetworkMaster;

public interface SOTNetworkMasterDao  extends JpaRepository<SOTNetworkMaster, Integer> {
   
}