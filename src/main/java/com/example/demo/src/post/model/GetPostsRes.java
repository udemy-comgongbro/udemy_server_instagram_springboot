package com.example.demo.src.post.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetPostsRes {
    private int postIdx;
    private int userIdx;
    private String nickName;
    private String profileImgUrl;
    private String content;
    private int postLikeCount;
    private int commentCount;
    private String updatedAt;
    private String likeOrNot;
    private List<GetPostImgRes> imgs;

}
