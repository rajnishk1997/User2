package com.optum.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.optum.dao.ReqRes;
import com.optum.dto.response.GppJson28FieldValidationResponse;
import com.optum.entity.ResponseWrapper;
import com.optum.service.GppJson28Service;

import java.util.List;

@RestController
@RequestMapping("/api/gpp")
public class GppJson28Controller {

    @Autowired
    private GppJson28Service service;

    @GetMapping("/json28/{uid}")
    public ResponseEntity<ResponseWrapper<List<GppJson28FieldValidationResponse>>> getGppJson28ByUid(@PathVariable String uid) {
        try {
            List<GppJson28FieldValidationResponse> gppJson28List = service.getGppJson28ByUid(uid);
            ResponseWrapper<List<GppJson28FieldValidationResponse>> response = new ResponseWrapper<>(gppJson28List, new ReqRes());
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            throw new RuntimeException("Failed to fetch gpp_json28 data", ex);
        }
    }
}