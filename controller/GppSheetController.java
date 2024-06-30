package com.optum.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.optum.dto.GppSheetDto;
import com.optum.service.GppSheetService;

import java.util.List;

@RestController
@RequestMapping("/api/gpp-sheets")
public class GppSheetController {

    @Autowired
    private GppSheetService gppSheetService;
    
    @GetMapping("/getAllGppSheets")
    public ResponseEntity<List<GppSheetDto>> getAllGppSheets() {
        try {
            List<GppSheetDto> gppSheets = gppSheetService.getAllGppSheets();
            return new ResponseEntity<>(gppSheets, HttpStatus.OK);
        } catch (Exception e) {
            // Log the exception or handle it as per your application's error handling strategy
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

