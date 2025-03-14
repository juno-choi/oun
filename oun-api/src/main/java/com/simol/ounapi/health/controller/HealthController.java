package com.simol.ounapi.health.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.simol.ouncommon.api.CommonApi;
import com.simol.ouncommon.api.ErrorApi;
import com.simol.ouncommon.health.dto.HealthCreateRequest;
import com.simol.ouncommon.health.dto.HealthUpdateRequest;
import com.simol.ouncommon.health.service.HealthService;
import com.simol.ouncommon.health.vo.HealthCreateResponse;
import com.simol.ouncommon.health.vo.HealthListResponse;
import com.simol.ouncommon.health.vo.HealthResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/routine/health")
@RequiredArgsConstructor
@Tag(name = "02. Health", description = "운동 관리 API")
@SecurityRequirement(name = "X-User-Id")
@SecurityRequirement(name = "X-User-Role")
@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
public class HealthController {
    private final HealthService healthService;
    
    @PostMapping
    @Operation(summary = "1. 운동 생성", description = "운동을 생성합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "success", content = @Content(schema = @Schema(implementation = HealthCreateResponse.class))),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = ErrorApi.class))),
    })
    public ResponseEntity<CommonApi<HealthCreateResponse>> createHealth(@RequestBody @Validated HealthCreateRequest healthCreateRequest, HttpServletRequest request, BindingResult bindingResult) {
        HealthCreateResponse healthCreateResponse = healthService.createHealth(healthCreateRequest, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(CommonApi.create(healthCreateResponse));
    }

    @GetMapping("/{health_id}")
    @Operation(summary = "2. 운동 조회", description = "운동을 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "success", content = @Content(schema = @Schema(implementation = HealthResponse.class))),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = ErrorApi.class))),
    })
    public ResponseEntity<CommonApi<HealthResponse>> getHealth(@Schema(description = "운동 id", example = "1") @PathVariable(name = "health_id") Long healthId) {
        HealthResponse healthResponse = healthService.getHealth(healthId);
        return ResponseEntity.ok(CommonApi.success(healthResponse));
    }

    @GetMapping("")
    @Operation(summary = "3. 운동 목록 조회", description = "운동 목록을 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "success", content = @Content(schema = @Schema(implementation = HealthListResponse.class))),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = ErrorApi.class))),
    })
    public ResponseEntity<CommonApi<HealthListResponse>> getHealthList(
        @Schema(description = "routine ID", example = "1")
        @RequestParam(name = "routine_id") Long routineId,
        @Schema(description = "페이지", example = "0")
        @RequestParam(name = "page", defaultValue = "0") int page,
        @Schema(description = "페이지 크기", example = "10")
        @RequestParam(name = "size", defaultValue = "10") int size) {
        HealthListResponse healthListResponse = healthService.getHealthList(routineId, page, size);
        return ResponseEntity.ok(CommonApi.success(healthListResponse));
    }

    @PutMapping("")
    @Operation(summary = "4. 운동 수정", description = "운동을 수정합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "success", content = @Content(schema = @Schema(implementation = HealthResponse.class))),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = ErrorApi.class))),
    })
    public ResponseEntity<CommonApi<HealthResponse>> updateHealth(@RequestBody @Validated HealthUpdateRequest healthUpdateRequest, HttpServletRequest request, BindingResult bindingResult) {
        HealthResponse healthResponse = healthService.updateHealth(healthUpdateRequest, request);
        return ResponseEntity.ok(CommonApi.success(healthResponse));
    }
    
    @DeleteMapping("/{health_id}")
    @Operation(summary = "5. 운동 삭제", description = "운동을 삭제합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "success", content = @Content(schema = @Schema(implementation = HealthResponse.class))),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = ErrorApi.class))),
    })
    public ResponseEntity<CommonApi<Void>> deleteHealth(@Schema(description = "운동 id", example = "1") @PathVariable(name = "health_id") Long healthId) {
        healthService.deleteHealth(healthId);
        return ResponseEntity.ok(CommonApi.success(null));
    }
}
