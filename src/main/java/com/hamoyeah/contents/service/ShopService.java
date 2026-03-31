package com.hamoyeah.contents.service;

import com.hamoyeah.contents.Entity.ShopCategory;
import com.hamoyeah.contents.Entity.ShopEntity;
import com.hamoyeah.contents.repository.ShopRepository;
import com.hamoyeah.util.주소좌표변환.GeocodingService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ShopService {
    private final ShopRepository shopRepository;
    private final GeocodingService geocodingService;

    public void importShopFromExcel(InputStream is) {
        try (Workbook workbook = WorkbookFactory.create(is)) { // try-with-resources로 자동 close
            Sheet sheet = workbook.getSheetAt(0);
            List<ShopEntity> shopEntityList = new ArrayList<>();

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                String name = "";
                try {
                    // 1. 셀 데이터 읽기 (NPE 방어)
                    name = getCellValue(row, 2, "상호명 없음");
                    String rawCategory = getCellValue(row, 3, "기타");
                    String rawAddress = getCellValue(row, 4, "");

                    if (rawAddress.isEmpty()) {
                        System.out.println(i + "행: 주소가 없어서 건너뜁니다.");
                        continue;
                    }

                    String address = rawAddress.replaceAll("\\(.*?\\)", "").trim();

                    ShopEntity entity = ShopEntity.builder()
                            .name(name)
                            .rawCategory(rawCategory)
                            .shopCategory(ShopCategory.fromRawCategory(rawCategory))
                            .address(address)
                            .build();

                    // 2. 좌표 변환 및 리스트 추가
                    geocodingService.fillCoordinates(entity);
                    shopEntityList.add(entity);

                    Thread.sleep(50); // API 호출 제한 방지

                } catch (Exception e) {
                    log.warn("[Excel Import] {}행 처리 중 스킵됨. 사유: {}, 상호명: {}", i, e.getMessage(), name);
                }
            }

            // 3. 한꺼번에 저장
            if (!shopEntityList.isEmpty()) {
                shopRepository.saveAll(shopEntityList);
                log.info("엑셀 임포트 완료: 총 {}개의 상점 데이터를 저장했습니다.", shopEntityList.size());
            }

        } catch (IOException e) {
            log.error("엑셀 파일 로드 실패 - 파일이 손상되었거나 형식이 맞지 않습니다.", e);
        } catch (Exception e) {
            log.error("엑셀 임포트 중 예기치 못한 시스템 오류 발생", e);
        }
    }

    // 셀 값을 안전하게 가져오는 헬퍼 메소드
    private String getCellValue(Row row, int cellIndex, String defaultValue) {
        Cell cell = row.getCell(cellIndex);
        if (cell == null || cell.getCellType() == CellType.BLANK) {
            return defaultValue;
        }
        return cell.toString().trim();
    }
}
