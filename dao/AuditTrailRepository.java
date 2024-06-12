package com.optum.dao;

import com.optum.entity.AuditTrail;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditTrailRepository extends JpaRepository<AuditTrail, Long> {
	
	 Page<AuditTrail> findAllByOrderByTimestampDesc(Pageable pageable);
	 Page<AuditTrail> findAll(Specification<AuditTrail> spec, Pageable pageable);
}
