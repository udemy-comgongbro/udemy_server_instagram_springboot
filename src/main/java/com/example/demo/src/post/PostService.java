package com.example.demo.src.post;


import com.example.demo.config.BaseException;
import com.example.demo.config.secret.Secret;
import com.example.demo.src.post.model.*;
import com.example.demo.src.user.model.PatchUserReq;
import com.example.demo.utils.AES128;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.example.demo.config.BaseResponseStatus.*;

// Service Create, Update, Delete 의 로직 처리
@Service
public class PostService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final PostDao postDao;
    private final PostProvider postProvider;
    private final JwtService jwtService;


    @Autowired
    public PostService(PostDao postDao, PostProvider postProvider, JwtService jwtService) {
        this.postDao = postDao;
        this.postProvider = postProvider;
        this.jwtService = jwtService;

    }

    //게시글 작성
    public PostPostRes createPost(int userIdx, PostPostReq postPostReq) throws BaseException {


        try{
            int postIdx = postDao.insertPost(userIdx, postPostReq);
            for(int i=0; i< postPostReq.getPostImgsUrl().size(); i++) {
                postDao.insertPostImgs(postIdx, postPostReq.getPostImgsUrl().get(i));
            }
            return new PostPostRes(postIdx);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }


    // 게시물 수정
    public void modifyPost(int userIdx,int postIdx, PatchPostReq patchPostReq) throws BaseException {
        if(postProvider.checkUserExist(userIdx) ==0){
            throw new BaseException(USERS_EMPTY_USER_ID);
        }
        if(postProvider.checkPostExist(postIdx) ==0){
            throw new BaseException(POSTS_EMPTY_POST_ID);
        }

        if(postProvider.checkUserPostExist(userIdx, postIdx)==0){
            throw new BaseException(POSTS_EMPTY_USER_POST);
        }
        try{
            int result = postDao.updatePost(postIdx,patchPostReq);
            if(result == 0){
                throw new BaseException(MODIFY_FAIL_POST);
            }
        } catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 회원 삭제
    public void deletePost(int userIdx,int postIdx) throws BaseException {
        if(postProvider.checkUserExist(userIdx) ==0){
            throw new BaseException(USERS_EMPTY_USER_ID);
        }
        if(postProvider.checkPostExist(postIdx) ==0){
            throw new BaseException(POSTS_EMPTY_POST_ID);
        }

        if(postProvider.checkUserPostExist(userIdx, postIdx)==0){
            throw new BaseException(POSTS_EMPTY_USER_POST);
        }
        try{
            int result = postDao.updatePostStatus(postIdx);
            if(result == 0){
                throw new BaseException(DELETE_FAIL_POST);
            }
        } catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
