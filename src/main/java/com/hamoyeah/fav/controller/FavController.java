package com.hamoyeah.fav.controller;

import com.hamoyeah.fav.dto.FavDto;
import com.hamoyeah.fav.entity.FavEntity;
import com.hamoyeah.fav.service.FavService;
import com.hamoyeah.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/fav")
@CrossOrigin(origins = "http://localhost:5173")
public class FavController {
    private final FavService favService;
    private final UserService userService;

    // 즐겨찾기 등록
    @PostMapping()
    public ResponseEntity<?> register(
            @RequestHeader(value="Authorization", required=false) String bearerToken,
            @RequestBody FavDto favDto){
        if(bearerToken==null||!bearerToken.startsWith("Bearer ")){
            return ResponseEntity.status(400).body("토큰이 없거나 형식이 잘못되었습니다.");
        }
        String token=bearerToken.substring(7);
        String email=userService.getClaim(token);
        if(email==null){
            return ResponseEntity.status(401).body("유효하지 않은 토큰입니다.");
        }
        FavEntity result=favService.register(email, favDto);
        if(result!=null){
            return ResponseEntity.ok("즐겨찾기 등록이 되었습니다.");
        } else {
            return ResponseEntity.status(500).body("즐겨찾기 등록이 실패하였습니다.");
        }
    }
}
