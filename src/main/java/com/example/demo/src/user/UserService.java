package com.example.demo.src.user;


import com.example.demo.config.BaseException;
import com.example.demo.config.secret.Secret;
import com.example.demo.src.user.model.*;
import com.example.demo.utils.AES128;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import static com.example.demo.config.BaseResponseStatus.*;

// Service Create, Update, Delete 의 로직 처리
@Service
public class UserService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final UserDao userDao;
    private final UserProvider userProvider;
    private final JwtService jwtService;


    @Autowired
    public UserService(UserDao userDao, UserProvider userProvider, JwtService jwtService) {
        this.userDao = userDao;
        this.userProvider = userProvider;
        this.jwtService = jwtService;

    }

   // 회원 가입
    public PostUserRes createUser(PostUserReq postUserReq) throws BaseException {
        // 중복 확인
        if(userProvider.checkEmailExist(postUserReq.getEmail()) ==1){
            throw new BaseException(DUPLICATED_EMAIL);
        }

        String pwd;
        try{
            //암호화
            pwd = new AES128(Secret.USER_INFO_PASSWORD_KEY).encrypt(postUserReq.getPwd());
            postUserReq.setPwd(pwd);
        } catch (Exception ignored) {
            throw new BaseException(PASSWORD_ENCRYPTION_ERROR);
        }
        try{
            int userIdx = userDao.insertUser(postUserReq);
            //jwt 발급.
            String jwt = jwtService.createJwt(userIdx);
            return new PostUserRes(jwt,userIdx);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 프로필 수정
    public void modifyProfile(int userIdx,PatchUserReq patchUserReq) throws BaseException {
        if(userProvider.checkUserExist(userIdx) ==0){
            throw new BaseException(USERS_EMPTY_USER_ID);
        }
        if(userProvider.checkNickNameExist(patchUserReq.getNickName()) ==1){
            throw new BaseException(DUPLICATED_NICKNAME);
        }
        try{
            int result = userDao.updateProfile(userIdx,patchUserReq);
            if(result == 0){
                throw new BaseException(MODIFY_FAIL_USERNAME);
            }
        } catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 회원 삭제
    public void deleteUser(int userIdx) throws BaseException {
        if(userProvider.checkUserExist(userIdx) ==1){
            throw new BaseException(USERS_EMPTY_USER_ID);
        }
        try{
            int result = userDao.updateUserStatus(userIdx);
            if(result == 0){
                throw new BaseException(DELETE_FAIL_USER);
            }
        } catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
