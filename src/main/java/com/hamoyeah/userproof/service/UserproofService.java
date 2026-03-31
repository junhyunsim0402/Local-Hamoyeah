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
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserproofService {
    private final UserproofRepository userproofRepository;
    private final UserRepository userRepository;
    private final ContentsRepository contentsRepository;
    private final FileService fileService;

    // 유저 사진 등록 기능
    @Transactional
    public UserproofEntity signup(String email, UserProofDto userProofDto) {
        Optional<UserEntity> userpro = userRepository.findByEmail(email);
        Optional<ContentsEntity> contentpro = contentsRepository.findById(userProofDto.getContentId());

        String filename = fileService.upload(userProofDto.getUploadimg());
        if(filename==null){return null;}

        if (userpro.isPresent() && contentpro.isPresent()) {
            UserproofEntity entity = UserproofEntity.builder()
                    .userEntity(userpro.get())
                    .contentsEntity(contentpro.get())
                    .imageUrl(filename)
                    .status("대기중")
                    .build();
            return userproofRepository.save(entity);
        }
        return null;
    }
}
