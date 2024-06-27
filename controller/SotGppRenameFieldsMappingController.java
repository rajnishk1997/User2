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
import com.optum.dto.SotGppRenameFieldsMappingDto;
import com.optum.entity.ResponseWrapper;
import com.optum.entity.SotGppRenameFieldsMapping;
import com.optum.service.SotGppRenameFieldsMappingService;

@RestController
@RequestMapping("/api/sotGppMappings")
public class SotGppRenameFieldsMappingController {
	
	private static final Logger logger = LogManager.getLogger(SotGppRenameFieldsMappingController.class);

	@Autowired
    private SotGppRenameFieldsMappingService service;

    @PostMapping("/createSotGppMapping")
    public ResponseEntity<ResponseWrapper<SotGppRenameFieldsMapping>> createMapping(@RequestBody SotGppRenameFieldsMappingDto dto) {
        Integer currentUserRid = dto.getCurrentUserId();
        long startTime = System.currentTimeMillis();
        try {
            SotGppRenameFieldsMapping createdMapping = service.createMapping(dto);

            ReqRes reqRes = new ReqRes(HttpStatus.CREATED.value(), "SUCCESS", "Mapping created successfully");
            ResponseWrapper<SotGppRenameFieldsMapping> response = new ResponseWrapper<>(createdMapping, reqRes);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            ReqRes reqRes = new ReqRes(HttpStatus.BAD_REQUEST.value(), "Bad Request", e.getMessage());
            ResponseWrapper<SotGppRenameFieldsMapping> response = new ResponseWrapper<>(null, reqRes);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            ReqRes reqRes = new ReqRes(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal Server Error", "An error occurred while creating the mapping");
            ResponseWrapper<SotGppRenameFieldsMapping> response = new ResponseWrapper<>(null, reqRes);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        } finally {
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            logger.info("Create Mapping Action performed in " + duration + "ms");
        }
    }

    @PutMapping("updateSotGppMapping/{id}")
    public ResponseEntity<ResponseWrapper<SotGppRenameFieldsMapping>> updateMapping(@PathVariable int id, @RequestBody SotGppRenameFieldsMappingDto dto) {
        Integer currentUserRid = dto.getCurrentUserId();
        long startTime = System.currentTimeMillis();
        try {
            SotGppRenameFieldsMapping updatedMapping = service.updateMapping(id, dto);

            ReqRes reqRes = new ReqRes(HttpStatus.OK.value(), "SUCCESS", "Mapping updated successfully");
            ResponseWrapper<SotGppRenameFieldsMapping> response = new ResponseWrapper<>(updatedMapping, reqRes);

            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (IllegalArgumentException e) {
            ReqRes reqRes = new ReqRes(HttpStatus.BAD_REQUEST.value(), "Bad Request", e.getMessage());
            ResponseWrapper<SotGppRenameFieldsMapping> response = new ResponseWrapper<>(null, reqRes);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            ReqRes reqRes = new ReqRes(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal Server Error", "An error occurred while updating the mapping");
            ResponseWrapper<SotGppRenameFieldsMapping> response = new ResponseWrapper<>(null, reqRes);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        } finally {
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            logger.info("Update Mapping Action performed in " + duration + "ms");
        }
    }

    @GetMapping("/getSotGppAllMappings")
    public ResponseEntity<ResponseWrapper<List<SotGppRenameFieldsMapping>>> getAllMappings() {
        long startTime = System.currentTimeMillis();
        try {
            List<SotGppRenameFieldsMapping> mappings = service.getAllMappings();

            ReqRes reqRes = new ReqRes(HttpStatus.OK.value(), "SUCCESS", "Retrieved all mappings");
            ResponseWrapper<List<SotGppRenameFieldsMapping>> response = new ResponseWrapper<>(mappings, reqRes);

            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            ReqRes reqRes = new ReqRes(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal Server Error", "An error occurred while retrieving mappings");
            ResponseWrapper<List<SotGppRenameFieldsMapping>> response = new ResponseWrapper<>(null, reqRes);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        } finally {
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            logger.info("Get All Mappings Action performed in " + duration + "ms");
        }
    }

    @GetMapping("getSotGppMappingById/{id}")
    public ResponseEntity<ResponseWrapper<SotGppRenameFieldsMapping>> getMappingById(@PathVariable int id) {
        long startTime = System.currentTimeMillis();
        try {
            SotGppRenameFieldsMapping mapping = service.getMappingById(id);

            ReqRes reqRes = new ReqRes(HttpStatus.OK.value(), "SUCCESS", "Retrieved mapping");
            ResponseWrapper<SotGppRenameFieldsMapping> response = new ResponseWrapper<>(mapping, reqRes);

            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (IllegalArgumentException e) {
            ReqRes reqRes = new ReqRes(HttpStatus.BAD_REQUEST.value(), "Bad Request", e.getMessage());
            ResponseWrapper<SotGppRenameFieldsMapping> response = new ResponseWrapper<>(null, reqRes);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            ReqRes reqRes = new ReqRes(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal Server Error", "An error occurred while retrieving the mapping");
            ResponseWrapper<SotGppRenameFieldsMapping> response = new ResponseWrapper<>(null, reqRes);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        } finally {
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            logger.info("Get Mapping by ID Action performed in " + duration + "ms");
        }
    }

    @GetMapping("/search")
    public ResponseEntity<ResponseWrapper<List<SotGppRenameFieldsMapping>>> searchMappings(@RequestParam String keyword) {
        long startTime = System.currentTimeMillis();
        try {
            List<SotGppRenameFieldsMapping> mappings = service.searchMappings(keyword);

            ReqRes reqRes = new ReqRes(HttpStatus.OK.value(), "SUCCESS", "Search results retrieved");
            ResponseWrapper<List<SotGppRenameFieldsMapping>> response = new ResponseWrapper<>(mappings, reqRes);

            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            ReqRes reqRes = new ReqRes(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal Server Error", "An error occurred while searching for mappings");
            ResponseWrapper<List<SotGppRenameFieldsMapping>> response = new ResponseWrapper<>(null, reqRes);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        } finally {
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            logger.info("Search Mappings Action performed in " + duration + "ms");
        }
    }
}
