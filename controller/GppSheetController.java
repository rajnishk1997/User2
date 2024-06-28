package com.optum.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.optum.dao.GppSheetDao;
import com.optum.entity.GppSheet;

import java.util.List;

@RestController
@RequestMapping("/api/gpp-sheets")
public class GppSheetController {

    private final GppSheetDao gppSheetRepository;

    @Autowired
    public GppSheetController(GppSheetDao gppSheetRepository) {
        this.gppSheetRepository = gppSheetRepository;
    }

    @GetMapping("/getAllGppSheets")
    public ResponseEntity<List<GppSheet>> getAllGppSheets() {
        try {
            List<GppSheet> gppSheets = gppSheetRepository.findAll();
            return new ResponseEntity<>(gppSheets, HttpStatus.OK);
        } catch (Exception e) {
            // Log the exception or handle it as per your application's error handling strategy
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

