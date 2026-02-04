package com.ecom.analytics.dto;

import java.util.List;

public record PageResult<T>(List<T> items, Long total, Integer limit, Integer offset, String nextCursor) {}
