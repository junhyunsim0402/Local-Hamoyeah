package com.hamoyeah.contents.service;

import com.hamoyeah.contents.Entity.ShopCategory;
import com.hamoyeah.contents.Entity.ShopEntity;
import com.hamoyeah.contents.repository.ShopRepository;
import com.hamoyeah.util.주소좌표변환.GeocodingService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ShopService {
    private final ShopRepository shopRepository;
    private final GeocodingService geocodingService;

    public void importShopFromExcel(InputStream is) {
        try {
            Workbook workbook = WorkbookFactory.create(is);
            Sheet sheet = workbook.getSheetAt(0);

            List<ShopEntity> shopEntityList = new ArrayList<>();

            for (int i = 1; i <= sheet.getLastRowNum(); i++) { // 0번은 헤더니깐 1행부터
                Row row = sheet.getRow(i);
                if (row == null) continue;

                // 데이터 읽기
                String name = (row.getCell(2) != null) ? row.getCell(2).toString().trim() : "상호명 없음"; //가게명
                String rawCategory = row.getCell(3).toString().trim();//업종
                String rawAddress = row.getCell(4).toString().trim();//주소

                String address = rawAddress.replaceAll("\\(.*?\\)", "").trim(); // 괄호 제거

                ShopEntity entity = ShopEntity.builder()
                        .name(name)
                        .rawCategory(rawCategory)
                        .shopCategory(ShopCategory.fromRawCategory(rawCategory))
                        .address(address)
                        .build();

                geocodingService.fillCoordinates(entity);
                shopEntityList.add(entity);

                Thread.sleep(50);
            }
            shopRepository.saveAll(shopEntityList);
            workbook.close();
        } catch (Exception e){
            System.out.println(e);
        }
    }
}
