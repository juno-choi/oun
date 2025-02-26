package com.simol.ouncommon.health.enums;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum HealthDetailStatus {
    ACTIVE("활성"),
    INACTIVE("비활성")
    ;
    public String description;
}
