package com.example.demo.src.post;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.post.model.*;
import com.example.demo.src.user.model.PatchUserReq;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;
import static com.example.demo.utils.ValidationRegex.*;
import static com.example.demo.utils.ValidationRegex.isRegexNickName;


@RestController
@RequestMapping("/posts")
public class PostController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final PostProvider postProvider;
    @Autowired
    private final PostService postService;
    @Autowired
    private final JwtService jwtService;




    public PostController(PostProvider postProvider, PostService postService, JwtService jwtService){
        this.postProvider = postProvider;
        this.postService = postService;
        this.jwtService = jwtService;
    }




    @ResponseBody
    @GetMapping("")
    public BaseResponse<List<GetPostsRes>> getPosts(){
        try{
            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            List<GetPostsRes> getPosts=postProvider.retrievePosts(userIdxByJwt);

            return new BaseResponse<>(getPosts);
        } catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @ResponseBody
    @PostMapping("")
    public BaseResponse<PostPostRes> createPost(@RequestBody PostPostReq postPostReq) {
        if(postPostReq.getContent() == null){
            return new BaseResponse<>(POST_POSTS_EMPTY_CONTENTS);
        }
        if(postPostReq.getContent().length()>450){
            return new BaseResponse<>(POST_POSTS_EMPTY_CONTENTS);
        }
        if(postPostReq.getPostImgsUrl().size()<1){
            return new BaseResponse<>(POST_POSTS_EMPTY_IMGRUL);
        }

        try{
            int userIdxByJwt = jwtService.getUserIdx();
            PostPostRes postPostRes = postService.createPost(userIdxByJwt,postPostReq);
            return new BaseResponse<>(postPostRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    // 게시글 수정
    @ResponseBody
    @PatchMapping("/{postIdx}")
    public BaseResponse<String> modifyPost(@PathVariable("postIdx") int postIdx, @RequestBody PatchPostReq patchPostReq){
        if(patchPostReq.getContent() == null){
            return new BaseResponse<>(POST_POSTS_EMPTY_CONTENTS);
        }
        if(patchPostReq.getContent().length()>450){
            return new BaseResponse<>(POST_POSTS_EMPTY_CONTENTS);
        }
        try {
            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();

            postService.modifyPost(userIdxByJwt,postIdx,patchPostReq);
            String result = "회원정보 수정을 완료하였습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    // 게시물 삭제
    @ResponseBody
    @PatchMapping("/{postIdx}/status")
    public BaseResponse<String> deleteUser(@PathVariable("postIdx") int postIdx){
        try {

            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            postService.deletePost(userIdxByJwt,postIdx);

            String result = "삭제되었습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

}
