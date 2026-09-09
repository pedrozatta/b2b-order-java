package br.com.zattaz.common.model;

import java.util.List;

public record Page<T>(List<T> content, long totalElements, int pageNumber, int pageSize) {}
