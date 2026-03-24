package com.hamoyeah.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration      // spring 설정 파일 적용(WebMvcConfigurer)를 수정할 것
public class KakaoConfig implements WebMvcConfigurer {  // WebMvcConfigurer의 설정을 바꿀 것으로 선언
    @Override public void addCorsMappings(CorsRegistry registry){   // CorsRegistry안의 함수를 재정의
        registry.addMapping("/**")      // 내가 가진 프로젝트들을 모두 접근 호영
                .allowedOrigins("http://localhost:5173/")       // 리엑트 포트번호 허용
                .allowedMethods("GET","POST","PUT","DELETE")    // 보내는 요청 허용
                .allowedHeaders("*");                   // 탈란드에서 보내는 모든 컨탠츠 타입을 전부 허용
    }
}
