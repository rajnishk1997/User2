package com.optum.controller;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
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
	    private SotGppRenameFieldsMappingService sotGppRenameFieldsMappingService;
	    
		@PostMapping("/createSotGppMapping")
		public ResponseEntity<ReqRes> createSotGppMapping(@RequestBody SotGppRenameFieldsMappingDto createDto) {
		    try {
		        ResponseWrapper<SotGppRenameFieldsMapping> response = sotGppRenameFieldsMappingService.saveSotGppMapping(createDto);
		        ReqRes reqRes = new ReqRes(HttpStatus.OK.value(), null, "Mapping done successfully");
		        return ResponseEntity.status(HttpStatus.CREATED).body(reqRes);
		    } catch (IllegalArgumentException e) {
		        ReqRes reqRes = new ReqRes(HttpStatus.BAD_REQUEST.value(), "Bad Request", e.getMessage());
		        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(reqRes);
		    } catch (Exception e) {
		        ReqRes reqRes = new ReqRes(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal Server Error",
		                "An error occurred while creating the SOT-GPP Mapping");
		        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(reqRes);
		    }
		}

		@PutMapping("updateSotGppMapping/{sotGppRid}")
		public ResponseEntity<ResponseWrapper<SotGppRenameFieldsMapping>> updateSotGppMapping(
		        @PathVariable int sotGppRid, @RequestBody SotGppRenameFieldsMappingDto updateDto) {
		    try {
		        ResponseWrapper<SotGppRenameFieldsMapping> response = sotGppRenameFieldsMappingService.updateSotGppMapping(sotGppRid, updateDto);
		        return ResponseEntity.status(HttpStatus.OK).body(response);
		    } catch (IllegalArgumentException e) {
		        ReqRes reqRes = new ReqRes(HttpStatus.BAD_REQUEST.value(), "Bad Request", e.getMessage());
		        ResponseWrapper<SotGppRenameFieldsMapping> response = new ResponseWrapper<>(null, reqRes);
		        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		    } catch (Exception e) {
		        ReqRes reqRes = new ReqRes(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal Server Error",
		                "An error occurred while updating the SOT-GPP Mapping");
		        ResponseWrapper<SotGppRenameFieldsMapping> response = new ResponseWrapper<>(null, reqRes);
		        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		    }
		}

	    
		@GetMapping("/mappings/search")
		public ResponseEntity<List<SotGppRenameFieldsMapping>> searchMappings(
		        @RequestParam(required = false) String sotRename,
		        @RequestParam(required = false) String gppRename) {
		    try {
		        List<SotGppRenameFieldsMapping> mappings = sotGppRenameFieldsMappingService.searchMappings(sotRename, gppRename);
		        return new ResponseEntity<>(mappings, HttpStatus.OK);
		    } catch (Exception e) {
		        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		    }
		}
		
		  @GetMapping("/{sotGppRid}")
		    public ResponseEntity<ResponseWrapper<SotGppRenameFieldsMappingDto>> getMappingById(@PathVariable int sotGppRid) {
		        try {
		            SotGppRenameFieldsMappingDto mappingDto = sotGppRenameFieldsMappingService.getMappingById(sotGppRid);
		            ReqRes reqRes = new ReqRes(HttpStatus.OK.value(), "SUCCESS", "Mapping fetched successfully");
		            ResponseWrapper<SotGppRenameFieldsMappingDto> response = new ResponseWrapper<>(mappingDto, reqRes);
		            return ResponseEntity.ok(response);
		        } catch (IllegalArgumentException e) {
		            ReqRes reqRes = new ReqRes(HttpStatus.BAD_REQUEST.value(), "Bad Request", e.getMessage());
		            ResponseWrapper<SotGppRenameFieldsMappingDto> response = new ResponseWrapper<>(null, reqRes);
		            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		        } catch (Exception e) {
		            ReqRes reqRes = new ReqRes(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal Server Error",
		                    "An error occurred while fetching the SOT-GPP Mapping");
		            ResponseWrapper<SotGppRenameFieldsMappingDto> response = new ResponseWrapper<>(null, reqRes);
		            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		        }
		    }
		  
		  @DeleteMapping("/deleteSotGppMapping/{sotGppRid}")
		    public ResponseEntity<ResponseWrapper<String>> deleteSotGppMapping(@PathVariable int sotGppRid) {
		        long startTime = System.currentTimeMillis();
		        try {
		            if (!sotGppRenameFieldsMappingService.existsById(sotGppRid)) {
		                ReqRes reqRes = new ReqRes(HttpStatus.NOT_FOUND.value(), "Not Found", "SOT-GPP mapping not found");
		                ResponseWrapper<String> response = new ResponseWrapper<>(null, reqRes);
		                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
		            }

		            sotGppRenameFieldsMappingService.deleteById(sotGppRid);
		            ReqRes reqRes = new ReqRes(HttpStatus.OK.value(), "SUCCESS", "Deleted SOT-GPP mapping successfully");
		            ResponseWrapper<String> response = new ResponseWrapper<>("Deleted successfully", reqRes);
		            return ResponseEntity.status(HttpStatus.OK).body(response);
		        } catch (Exception e) {
		            ReqRes reqRes = new ReqRes(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal Server Error", "An error occurred while deleting SOT-GPP mapping");
		            ResponseWrapper<String> response = new ResponseWrapper<>(null, reqRes);
		            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		        } finally {
		            long endTime = System.currentTimeMillis();
		            long duration = endTime - startTime;
		            logger.info("Processed delete SOT-GPP mapping request in " + duration + "ms");
		        }
		    }

}
