package com.hamoyeah.contents.Entity;

import lombok.Getter;

@Getter
public enum ShopCategory {
    FOOD("음식점"),
    CAFE("카페/디저트"),
    STORE("편의점/마트"),
    MEDICAL("의료/약국"),
    LIFE("생활/미용"),
    ETC("기타");

    private final String description;

    ShopCategory(String description) {
        this.description = description;
    }

    public static ShopCategory fromRawCategory(String raw) {
        if (raw == null || raw.isEmpty() || raw.equals("업종")) return ETC;

        // 1. MEDICAL (의료/약국/건강/운동)
        if (raw.matches(".*(병원|의원|치과|한의원|약국|의료|안경|렌즈|헬스|필라테스|요가|체육시설|스포츠시설|체형교정|마사지|독서실).*")) {
            return MEDICAL;
        }

        // 2. STORE (식자재/유통/생활잡화)
        if (raw.matches(".*(편의점|마트|슈퍼|정육|축산|수산|농산물|과일|채소|식품|식자재|떡|반찬|참기름|고춧가루|건어물|생활잡화|일용잡화|비닐|농약|비료).*")) {
            return STORE;
        }

        // 3. LIFE (생활 서비스/여가/전문점/B2B 등 대통합)
        if (raw.matches(".*(미용|헤어|세탁|부동산|사진|스튜디오|주유|가스|수리|정비|자동차|세차|인테리어|꽃|화훼|숙박|호텔|펜션|목욕탕|운송|택시|인쇄|서점|문구|완구|귀금속|컴퓨터|가구|의류|패션|화장품|신발|가방|액세서리|PC방|피씨방|노래연습장|간판|광고|건설|건축|자재|철물|전기|공방|공예|취미|공부방|과외|기념품|판촉물|창작|예술|문화|공연|체험|서비스|냉난방|에어컨|농기구|기계|드론|상담|캠핑|레저|스포츠용품|스포츠교육|애완|반려동물|여행사|영상|장소대여|주방용품|직물|천막|청소|페인트|휴대폰|통신기기).*")) {
            return LIFE;
        }

        // 4. FOOD (음식점/카페)
        if (raw.matches(".*(음식점|치킨|통닭|분식|한식|중식|일식|양식|카페|디저트|음료|빙과).*")) {
            return (raw.contains("카페") || raw.contains("디저트") || raw.contains("음료")) ? CAFE : FOOD;
        }

        return ETC;
    }
}
