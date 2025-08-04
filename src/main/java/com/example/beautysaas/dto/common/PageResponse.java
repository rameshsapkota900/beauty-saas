package com.example.beautysaas.dto.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageResponse<T> {
    private List<T> content;
    private PageMetadata metadata;
    
    public static <T> PageResponse<T> of(List<T> content, PageMetadata metadata) {
        return PageResponse.<T>builder()
                .content(content)
                .metadata(metadata)
                .build();
    }
}
