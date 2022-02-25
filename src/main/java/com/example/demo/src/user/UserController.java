package com.example.demo.src.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.user.model.*;
import com.example.demo.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import static com.example.demo.config.BaseResponseStatus.*;
import static com.example.demo.utils.ValidationRegex.*;

@RestController
@RequestMapping("/users")
public class UserController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final UserProvider userProvider;
    @Autowired
    private final UserService userService;
    @Autowired
    private final JwtService jwtService;




    public UserController(UserProvider userProvider, UserService userService, JwtService jwtService){
        this.userProvider = userProvider;
        this.userService = userService;
        this.jwtService = jwtService;
    }


    // 회원 피드 조회
    @ResponseBody
    @GetMapping("/{userIdx}")
    public BaseResponse<GetUserFeedRes> getUserFeed(@PathVariable("userIdx") int userIdx){
        try {
             /* TODO: jwt는 다음주차에서 배울 내용입니다!
            jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            GetUserFeedRes getUserFeed=userProvider.retrieveUserFeed(userIdx,userIdxByJwt);
               TODO: 우선 아래 코드로 진행해주세요!
            */

            GetUserFeedRes getUserFeed=userProvider.retrieveUserFeed(userIdx,userIdx);

            return new BaseResponse<>(getUserFeed);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    // 회원 가입
    @ResponseBody
    @PostMapping("")
    public BaseResponse<PostUserRes> createUser(@RequestBody PostUserReq postUserReq) {
        // TODO: 짧은 validation 예시입니다. 그 외 더 부가적으로 추가해주세요!
        if(postUserReq.getEmail() == null){
            return new BaseResponse<>(POST_USERS_EMPTY_EMAIL);
        }
        if(postUserReq.getPwd() == null){
            return new BaseResponse<>(POST_USERS_EMPTY_PASSWORD);
        }
        if(postUserReq.getName() == null){
            return new BaseResponse<>(POST_USERS_EMPTY_NAME);
        }

        if(postUserReq.getBirth() == null){
            return new BaseResponse<>(POST_USERS_EMPTY_BIRTHDAY);
        }
        if(postUserReq.getNickName() == null){
            return new BaseResponse<>(POST_USERS_EMPTY_NICKNAME);
        }

        // 정규 표현
        if(!isRegexEmail(postUserReq.getEmail())){
            return new BaseResponse<>(POST_USERS_INVALID_EMAIL);
        }
        if(!isRegexPassword(postUserReq.getPwd())){
            return new BaseResponse<>(POST_USERS_INVALID_PASSWORD);
        }
        if(!isRegexName(postUserReq.getName())){
            return new BaseResponse<>(POST_USERS_INVALID_NAME);
        }
        if(!isRegexNickName(postUserReq.getNickName())){
            return new BaseResponse<>(POST_USERS_INVALID_NICKNAME);
        }

        try{
            PostUserRes postUserRes = userService.createUser(postUserReq);
            return new BaseResponse<>(postUserRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }


    // 프로필 수정
    @ResponseBody
    @PatchMapping("/{userIdx}")
    public BaseResponse<String> modifyProfile(@PathVariable("userIdx") int userIdx, @RequestBody PatchUserReq patchUserReq){
        if(patchUserReq.getName() == null){
            return new BaseResponse<>(POST_USERS_EMPTY_NAME);
        }

        if(patchUserReq.getNickName() == null){
            return new BaseResponse<>(POST_USERS_EMPTY_NICKNAME);
        }
        if(patchUserReq.getProfileImgUrl() == null){
            return new BaseResponse<>(PATCH_USERS_EMPTY_PROFILEIMG);
        }
        if(patchUserReq.getWebsite() == null){
            return new BaseResponse<>(PATCH_USERS_EMPTY_WEBSITE);
        }
        if(patchUserReq.getIntroduction() == null){
            return new BaseResponse<>(PATCH_USERS_EMPTY_INTRODUCE);
        }
        if(!isRegexName(patchUserReq.getName())){
            return new BaseResponse<>(POST_USERS_INVALID_NAME);
        }
        if(!isRegexNickName(patchUserReq.getNickName())){
            return new BaseResponse<>(POST_USERS_INVALID_NICKNAME);
        }
        if(!isRegexUrl(patchUserReq.getProfileImgUrl())){
            return new BaseResponse<>(PATCH_USERS_INVALID_PROFILEIMG);
        }
        if(!isRegexUrl(patchUserReq.getWebsite())){
            return new BaseResponse<>(PATCH_USERS_INVALID_WEBSITE);
        }
        try {
              /* TODO: jwt는 다음주차에서 배울 내용입니다!
            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(userIdx != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
               */
            userService.modifyProfile(userIdx,patchUserReq);
            String result = "회원정보 수정을 완료하였습니다.";
        return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }





    // 회원 삭제
    @ResponseBody
    @PatchMapping("/{userIdx}/status")
    public BaseResponse<String> deleteUser(@PathVariable("userIdx") int userIdx){
        try {
             /* TODO: jwt는 다음주차에서 배울 내용입니다!
            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(userIdx != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            //같다면 유저 삭제
            */
            userService.deleteUser(userIdx);

            String result = "삭제되었습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

}
