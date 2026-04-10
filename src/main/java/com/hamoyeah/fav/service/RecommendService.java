package com.hamoyeah.fav.service;

import com.hamoyeah.contents.repository.ContentsRepository;
import com.hamoyeah.fav.repository.FavRepository;
import com.hamoyeah.userproof.entity.UserproofEntity;
import com.hamoyeah.userproof.repository.UserproofRepository;
import com.hamoyeah.contents.Entity.ContentsEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class RecommendService {

    private final UserproofRepository userproofRepository;
    private final FavRepository favRepository;
    private final ContentsRepository contentsRepository;

    public List<Map<String, Object>> getTop2() {
        // 1. 인증수 집계
        Map<Integer, Long> proofCountMap = new HashMap<>();
        List<UserproofEntity> approvedList = userproofRepository
                .findByStatusAndContentsEntityIsNotNull("승인");
        for (UserproofEntity proof : approvedList) {
            Integer contentId = proof.getContentsEntity().getContentId();
            proofCountMap.put(contentId, proofCountMap.getOrDefault(contentId, 0L) + 1);
        }

        // 2. 전체 콘텐츠 가져와서 합산
        List<ContentsEntity> allContents = contentsRepository.findAll();
        List<Map<String, Object>> scoreList = new ArrayList<>();

        for (ContentsEntity content : allContents) {
            Integer contentId = content.getContentId();
            long proofCount = proofCountMap.getOrDefault(contentId, 0L);
            long favCount = favRepository.countByContentsEntity_ContentId(contentId);
            long totalScore = proofCount + favCount;

            if (totalScore == 0) continue;  // 0이면 스킵

            Map<String, Object> item = new HashMap<>();
            item.put("contentId", contentId);
            item.put("contentTitle", content.getContentTitle());
            item.put("imgUrl", content.getImgUrl());
            item.put("totalScore", totalScore);
            scoreList.add(item);
        }

        // 3. 점수 내림차순 정렬 후 TOP2 반환
        scoreList.sort((a, b) -> Long.compare(
                (Long) b.get("totalScore"),
                (Long) a.get("totalScore")));

        return scoreList.size() >= 2 ? scoreList.subList(0, 2) : scoreList;
    }
}