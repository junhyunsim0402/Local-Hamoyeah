package com.hamoyeah.userproof.service;

import com.hamoyeah.contents.Entity.ContentsEntity;
import com.hamoyeah.contents.repository.ContentsRepository;
import com.hamoyeah.user.entity.UserEntity;
import com.hamoyeah.user.repository.UserRepository;
import com.hamoyeah.userproof.dto.UserProofDto;
import com.hamoyeah.userproof.entity.UserproofEntity;
import com.hamoyeah.userproof.repository.UserproofRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserproofService {
    @Autowired
    private final UserproofRepository userproofRepository;
    private final UserRepository userRepository;
    private final ContentsRepository contentsRepository;
    private final FileService fileService;

    // 유저 사진 등록 기능
    public UserproofEntity verify(String email, UserProofDto userProofDto) {
        Optional<UserEntity> userproof = userRepository.findByEmail(email);
        Optional<ContentsEntity> contentproof = contentsRepository.findById(userProofDto.getContentId());

        String filename = fileService.upload(userProofDto.getUploadimg());
        if(filename==null){return null;}

        if (userproof.isPresent() && contentproof.isPresent()) {
            UserproofEntity entity = UserproofEntity.builder()
                    .userEntity(userproof.get())
                    .contentsEntity(contentproof.get())
                    .imageUrl(filename)
                    .status("대기중")
                    .build();
            return userproofRepository.save(entity);
        }
        return null;
    }

    // 관리자 승인/반려 기능
    public void status(UserProofDto userProofDto, Integer adminId){
        UserproofEntity proof=userproofRepository.findById(userProofDto.getProofId())
                .orElseThrow(()-> new IllegalArgumentException("해당 인증 내역이 없습니다."));
        UserEntity admin=userRepository.findById(adminId)
                .orElseThrow(()-> new IllegalArgumentException("관리자 내역이 없습니다."));
        if("반려됨".equals(userProofDto.getStatus())){
            if(userProofDto.getRejectReason()==null||userProofDto.getRejectReason().trim().isEmpty()){
                throw new IllegalArgumentException("반려 사유 작성해야 합니다.");
            }
            proof.setStatus("반려됨");
            proof.setRejectReason(userProofDto.getRejectReason());
        } else if("승인됨".equals(userProofDto.getStatus())){
            proof.setStatus("승인됨");
            proof.setRejectReason(null);
        }
        proof.setAdminEntity(admin);
        proof.setReviewedAt(LocalDateTime.now());
    }
}
