package com.optum.controller;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.optum.dao.ReqRes;
import com.optum.dto.GppFieldDetailsDto;
import com.optum.dto.GppRenameDto;
import com.optum.entity.GppFieldDetails;
import com.optum.entity.ResponseWrapper;
import com.optum.service.GppFieldDetailsService;

@RestController
@RequestMapping("/api/gppFieldDetails")
public class GppFieldDetailsController {
	private static final Logger logger = LogManager.getLogger(GppFieldDetailsController.class);

    @Autowired
    private GppFieldDetailsService gppFieldDetailsService;

    @PostMapping
    public ResponseEntity<ResponseWrapper<GppFieldDetails>> createGppFieldDetails(@RequestBody GppFieldDetailsDto gppFieldDetailsDto) {
        Integer currentUserRid = gppFieldDetailsDto.getCurrentUserId();
        long startTime = System.currentTimeMillis();
        try {
            GppFieldDetails createdGppFieldDetails = gppFieldDetailsService.createGppFieldDetails(gppFieldDetailsDto, currentUserRid);

            ReqRes reqRes = new ReqRes(HttpStatus.CREATED.value(), "SUCCESS", "GPP Field created successfully");
            ResponseWrapper<GppFieldDetails> response = new ResponseWrapper<>(createdGppFieldDetails, reqRes);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            ReqRes reqRes = new ReqRes(HttpStatus.BAD_REQUEST.value(), "Bad Request", e.getMessage());
            ResponseWrapper<GppFieldDetails> response = new ResponseWrapper<>(null, reqRes);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            ReqRes reqRes = new ReqRes(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal Server Error", "An error occurred while creating the GPP Field");
            ResponseWrapper<GppFieldDetails> response = new ResponseWrapper<>(null, reqRes);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        } finally {
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            logger.info("Create GPP Field Details Action performed in " + duration + "ms");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseWrapper<GppFieldDetails>> updateGppFieldDetails(@PathVariable int id, @RequestBody GppFieldDetailsDto gppFieldDetailsDto) {
        Integer currentUserRid = gppFieldDetailsDto.getCurrentUserId();
        long startTime = System.currentTimeMillis();
        try {
            GppFieldDetails updatedGppFieldDetails = gppFieldDetailsService.updateGppFieldDetails(id, gppFieldDetailsDto, currentUserRid);

            ReqRes reqRes = new ReqRes(HttpStatus.OK.value(), "SUCCESS", "GPP Field updated successfully");
            ResponseWrapper<GppFieldDetails> response = new ResponseWrapper<>(updatedGppFieldDetails, reqRes);

            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (IllegalArgumentException e) {
            ReqRes reqRes = new ReqRes(HttpStatus.BAD_REQUEST.value(), "Bad Request", e.getMessage());
            ResponseWrapper<GppFieldDetails> response = new ResponseWrapper<>(null, reqRes);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            ReqRes reqRes = new ReqRes(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal Server Error", "An error occurred while updating the GPP Field");
            ResponseWrapper<GppFieldDetails> response = new ResponseWrapper<>(null, reqRes);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        } finally {
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            logger.info("Update GPP Field Details Action performed in " + duration + "ms");
        }
    }

    @GetMapping
    public ResponseEntity<ResponseWrapper<List<GppFieldDetails>>> getAllGppFieldDetails() {
        long startTime = System.currentTimeMillis();
        try {
            List<GppFieldDetails> gppFieldDetailsList = gppFieldDetailsService.getAllGppFieldDetails();

            ReqRes reqRes = new ReqRes(HttpStatus.OK.value(), "SUCCESS", "GPP Fields retrieved successfully");
            ResponseWrapper<List<GppFieldDetails>> response = new ResponseWrapper<>(gppFieldDetailsList, reqRes);

            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            ReqRes reqRes = new ReqRes(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal Server Error", "An error occurred while retrieving GPP Fields");
            ResponseWrapper<List<GppFieldDetails>> response = new ResponseWrapper<>(null, reqRes);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        } finally {
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            logger.info("Get All GPP Field Details Action performed in " + duration + "ms");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseWrapper<GppFieldDetails>> getGppFieldDetailsById(@PathVariable int id) {
        long startTime = System.currentTimeMillis();
        try {
            GppFieldDetails gppFieldDetails = gppFieldDetailsService.getGppFieldDetailsById(id);

            ReqRes reqRes = new ReqRes(HttpStatus.OK.value(), "SUCCESS", "GPP Field retrieved successfully");
            ResponseWrapper<GppFieldDetails> response = new ResponseWrapper<>(gppFieldDetails, reqRes);

            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (IllegalArgumentException e) {
            ReqRes reqRes = new ReqRes(HttpStatus.BAD_REQUEST.value(), "Bad Request", e.getMessage());
            ResponseWrapper<GppFieldDetails> response = new ResponseWrapper<>(null, reqRes);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            ReqRes reqRes = new ReqRes(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal Server Error", "An error occurred while retrieving the GPP Field");
            ResponseWrapper<GppFieldDetails> response = new ResponseWrapper<>(null, reqRes);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        } finally {
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            logger.info("Get GPP Field Details by ID Action performed in " + duration + "ms");
        }
    }

    @GetMapping("/search")
    public ResponseEntity<ResponseWrapper<List<GppFieldDetails>>> searchGppFieldDetails(
        @RequestParam(required = false) String keyword,
        @RequestParam(required = false) Boolean validate) {
        long startTime = System.currentTimeMillis();
        try {
            List<GppFieldDetails> gppFieldDetailsList = gppFieldDetailsService.searchGppFieldDetails(keyword, validate);

            ReqRes reqRes = new ReqRes(HttpStatus.OK.value(), "SUCCESS", "GPP Fields retrieved successfully");
            ResponseWrapper<List<GppFieldDetails>> response = new ResponseWrapper<>(gppFieldDetailsList, reqRes);

            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            ReqRes reqRes = new ReqRes(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal Server Error", "An error occurred while searching GPP Fields");
            ResponseWrapper<List<GppFieldDetails>> response = new ResponseWrapper<>(null, reqRes);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        } finally {
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            logger.info("Search GPP Field Details Action performed in " + duration + "ms");
        }
    }
    
    @GetMapping("/getGppRenames")
    public ResponseEntity<ResponseWrapper<List<GppRenameDto>>> getAllGppRenames() {
        long startTime = System.currentTimeMillis();
        try {
            List<GppRenameDto> gppRenames = gppFieldDetailsService.getAllGppRenames();
            ReqRes reqRes = new ReqRes(HttpStatus.OK.value(), "SUCCESS", "Fetched all GPP renames successfully");
            ResponseWrapper<List<GppRenameDto>> response = new ResponseWrapper<>(gppRenames, reqRes);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            ReqRes reqRes = new ReqRes(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal Server Error", "An error occurred while fetching GPP renames");
            ResponseWrapper<List<GppRenameDto>> response = new ResponseWrapper<>(null, reqRes);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        } finally {
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            logger.info("Fetched all GPP renames in " + duration + "ms");
        }
    }

}
