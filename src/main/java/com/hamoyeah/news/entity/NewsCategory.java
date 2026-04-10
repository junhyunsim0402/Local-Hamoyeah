package com.hamoyeah.news.entity;

import lombok.Getter;

@Getter
public enum NewsCategory {  // 관광지 -> (1,1) -> 키워드 검색
    TOURIST(1, 1, new String[]{"관광", "여행", "진주성", "진양호"}),
    FESTIVAL(1, 2, new String[]{"축제", "행사", "유등", "남강"}),
    HERITAGE(1, 3, new String[]{"문화재", "촉석루", "역사", "유적"}),
    SPORTS(2, 1, new String[]{"체육", "스포츠", "운동시설", "헬스"}),
    PUBLIC_ART(3, 1, new String[]{"공공미술", "벽화", "예술", "거리미술"}),
    ARCHITECTURE(3, 2, new String[]{"건축", "조형물", "미술관", "건축미술"});

    private final int contentType;
    private final int contentCategory;
    private final String[] keywords;

    NewsCategory(int contentType, int contentCategory, String[] keywords) {
        this.contentType = contentType;
        this.contentCategory = contentCategory;
        this.keywords = keywords;
    }

    public static NewsCategory from(int contentType, int contentCategory) {
        for (NewsCategory nc : values()) {
            if (nc.contentType == contentType && nc.contentCategory == contentCategory) {
                return nc;
            }
        }
        return null;
    }   // 모든 키워드를 검색하고 없으면 null 있으면 뉴스 목록 반환
}