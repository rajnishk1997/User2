package com.optum.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.optum.dao.ReqRes;
import com.optum.entity.ResponseWrapper;
import com.optum.service.AllJsonResultService;

@RestController
@RequestMapping("/api/all-json")
public class AllJsonResultController {

    private final AllJsonResultService allJsonResultService;

    @Autowired
    public AllJsonResultController(AllJsonResultService allJsonResultService) {
        this.allJsonResultService = allJsonResultService;
    }

    @GetMapping("/{number}/{uid}")
    public ResponseEntity<ResponseWrapper<JsonNode>> fetchJsonData(
            @PathVariable int number,
            @PathVariable String uid
    ) {
        try {
            ResponseWrapper<JsonNode> response = allJsonResultService.fetchJsonData(number, uid);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            // Handle invalid number parameter
            return ResponseEntity.badRequest().body(new ResponseWrapper<>(null, new ReqRes())); // Adjust ReqRes if needed
        } catch (Exception e) {
            // Handle other exceptions
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseWrapper<>(null, new ReqRes())); // Adjust ReqRes if needed
        }
    }
}
