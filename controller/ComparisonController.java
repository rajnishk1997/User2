package com.optum.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.optum.dto.request.ComparisonRequest;
import com.optum.dto.response.GppFieldValidationResponse;
import com.optum.service.ComparisonService;

import java.util.List;

@RestController
@RequestMapping("/api/compare")
public class ComparisonController {

    @Autowired
    private ComparisonService comparisonService;

    @PostMapping("/compareJson")
    public List<GppFieldValidationResponse> compare(@RequestBody ComparisonRequest request) {
        return comparisonService.compareJsonFromDb(request.getSotJsonId(), request.getGppJsonId(), request.getGppSheet());
    }
}
