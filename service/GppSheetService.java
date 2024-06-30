package com.optum.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.optum.dao.GppSheetDao;
import com.optum.dto.GppSheetDto;

@Service
public class GppSheetService {

	@Autowired
    private GppSheetDao gppSheetRepository;

    public List<GppSheetDto> getAllGppSheets() {
        return gppSheetRepository.findAllGppSheetDtos();
    }
}
