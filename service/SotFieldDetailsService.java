package com.optum.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.optum.dao.SotFieldDetailsDao;
import com.optum.dto.SotFieldDetailsDto;
import com.optum.entity.SotFieldDetails;

@Service
public class SotFieldDetailsService {

    @Autowired
    private SotFieldDetailsDao sotFieldDetailsRepository;

    public SotFieldDetails createSotFieldDetails(SotFieldDetailsDto sotFieldDetailsDto, Integer userId) {
    	 SotFieldDetails sotFieldDetails = new SotFieldDetails();
        sotFieldDetails.setSotFieldName(sotFieldDetailsDto.getSotFieldName());
        sotFieldDetails.setSotFieldRename(sotFieldDetailsDto.getSotFieldRename());
        sotFieldDetails.setSOTValidationRequired(sotFieldDetailsDto.isSOTValidationRequired());
        sotFieldDetails.setCreatedBy(userId);
        sotFieldDetails.setCreatedDate(new Date());
        return sotFieldDetailsRepository.save(sotFieldDetails);
    }
    
    public SotFieldDetails updateSotFieldDetails(int id, SotFieldDetailsDto sotFieldDetailsDto, Integer userId) {
        Optional<SotFieldDetails> optionalSotFieldDetails = sotFieldDetailsRepository.findById(id);
        if (optionalSotFieldDetails.isPresent()) {
            SotFieldDetails sotFieldDetails = optionalSotFieldDetails.get();
            sotFieldDetails.setSotFieldName(sotFieldDetailsDto.getSotFieldName());
            sotFieldDetails.setSotFieldRename(sotFieldDetailsDto.getSotFieldRename());
            sotFieldDetails.setSOTValidationRequired(sotFieldDetailsDto.isSOTValidationRequired());
            sotFieldDetails.setModifiedBy(userId);
            sotFieldDetails.setModifiedDate(new Date());
            return sotFieldDetailsRepository.save(sotFieldDetails);
        } else {
            throw new RuntimeException("SotFieldDetails not found with id " + id);
        }
    }
    
    public List<SotFieldDetails> getAllSotFieldDetails() {
        return sotFieldDetailsRepository.findAll();
    }
    
    public SotFieldDetails getSotFieldDetailsById(int id) {
        return sotFieldDetailsRepository.findById(id).orElseThrow(() -> new RuntimeException("SotFieldDetails not found with id " + id));
    }

    public List<SotFieldDetails> searchSotFieldDetails(String keyword, Boolean validation) {
        if ((keyword == null || keyword.isEmpty()) && validation == null) {
            return getAllSotFieldDetails();
        }
        return sotFieldDetailsRepository.searchByKeywordAndValidation(keyword, validation);
    }
}