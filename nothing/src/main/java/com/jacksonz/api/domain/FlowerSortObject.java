package com.foundation.api.domain;

import lombok.Data;

import java.util.LinkedHashMap;
import java.util.Map;

@Data
public class FlowerSortObject {
    private String id;

    private Map<String, String> map = new LinkedHashMap<>();
}
