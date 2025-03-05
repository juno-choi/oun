package com.simol.ouncommon.routine.vo;

import java.util.List;
import org.springframework.data.domain.Page;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.simol.ouncommon.routine.entity.RoutineEntity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "루틴 목록 조회 응답")
public class RoutineListResponse {
    @Schema(description = "마지막 페이지 여부", example = "false")
    private boolean isLast;
    @Schema(description = "비어있는 페이지 여부", example = "false")
    private boolean isEmpty;
    @Schema(description = "총 페이지 수", example = "10")
    private int totalPages;
    @Schema(description = "총 요소 수", example = "100")
    private long totalElements;
    @Schema(description = "현재 페이지의 요소 수", example = "10")
    private int numberOfElements;
    @Schema(description = "루틴 목록")
    private List<RoutineResponse> routineList;

    public static RoutineListResponse of(Page<RoutineEntity> routineEntityPage) {
        List<RoutineResponse> routineList = routineEntityPage.stream().map(RoutineResponse::of).toList();

        return RoutineListResponse.builder()
            .isLast(routineEntityPage.isLast())
            .isEmpty(routineEntityPage.isEmpty())
            .totalPages(routineEntityPage.getTotalPages())
            .totalElements(routineEntityPage.getTotalElements())
            .numberOfElements(routineEntityPage.getNumberOfElements())
            .routineList(routineList)
            .build();
    }


}
