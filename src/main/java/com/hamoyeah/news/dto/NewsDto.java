package com.hamoyeah.news.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor @Builder
public class NewsDto {
    private String title;
    private String url;
    private String date;
    private String thumbnail;
}
