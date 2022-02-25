package com.example.demo.src.post;


import com.example.demo.src.post.model.*;
import com.example.demo.src.user.model.PatchUserReq;
import com.example.demo.src.user.model.PostUserReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class PostDao {

    private JdbcTemplate jdbcTemplate;
    private List<GetPostImgRes> getPostImgRes;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }





    // 유저 확인
    public int checkUserExist(int userIdx){
        String checkUserExistQuery = "select exists(select userIdx from User where userIdx = ?)";
        int checkUserExistParams = userIdx;
        return this.jdbcTemplate.queryForObject(checkUserExistQuery,
                int.class,
                checkUserExistParams);

    }

    // 게시글 확인
    public int checkPostExist(int postIdx){
        String checkPostExistQuery = "select exists(select postIdx from Post where postIdx = ?)";
        int checkPostExistParams = postIdx;
        return this.jdbcTemplate.queryForObject(checkPostExistQuery,
                int.class,
                checkPostExistParams);

    }

    // 이메일 확인
    public int checkEmailExist(String email){
        String checkEmailQuery = "select exists(select email from User where email = ?)";
        String checkEmailParams = email;
        return this.jdbcTemplate.queryForObject(checkEmailQuery,
                int.class,
                checkEmailParams);

    }

    // 게시글 리스트 조회
    public List<GetPostsRes> selectPosts(int userIdx){
        String selectUserPostsQuery = "\n" +
                "        SELECT p.postIdx as postIdx,\n" +
                "            u.userIdx as userIdx,\n" +
                "            u.nickName as nickName,\n" +
                "            u.profileImgUrl as profileImgUrl,\n" +
                "            p.content as content,\n" +
                "            IF(postLikeCount is null, 0, postLikeCount) as postLikeCount,\n" +
                "            IF(commentCount is null, 0, commentCount) as commentCount,\n" +
                "            case\n" +
                "                when timestampdiff(second, p.updatedAt, current_timestamp) < 60\n" +
                "                    then concat(timestampdiff(second, p.updatedAt, current_timestamp), '초 전')\n" +
                "                when timestampdiff(minute , p.updatedAt, current_timestamp) < 60\n" +
                "                    then concat(timestampdiff(minute, p.updatedAt, current_timestamp), '분 전')\n" +
                "                when timestampdiff(hour , p.updatedAt, current_timestamp) < 24\n" +
                "                    then concat(timestampdiff(hour, p.updatedAt, current_timestamp), '시간 전')\n" +
                "                when timestampdiff(day , p.updatedAt, current_timestamp) < 365\n" +
                "                    then concat(timestampdiff(day, p.updatedAt, current_timestamp), '일 전')\n" +
                "                else timestampdiff(year , p.updatedAt, current_timestamp)\n" +
                "            end as updatedAt,\n" +
                "            IF(pl.status = 'ACTIVE', 'Y', 'N') as likeOrNot\n" +
                "        FROM Post as p\n" +
                "            join User as u on u.userIdx = p.userIdx\n" +
                "            left join (select postIdx, userIdx, count(postLikeidx) as postLikeCount from PostLike WHERE status = 'ACTIVE' group by postIdx) plc on plc.postIdx = p.postIdx\n" +
                "            left join (select postIdx, count(commentIdx) as commentCount from Comment WHERE status = 'ACTIVE' group by postIdx) c on c.postIdx = p.postIdx\n" +
                "            left join Follow as f on f.followeeIdx = p.userIdx and f.status = 'ACTIVE'\n" +
                "            left join PostLike as pl on pl.userIdx = f.followerIdx and pl.postIdx = p.postIdx\n" +
                "        WHERE f.followerIdx = ? and p.status = 'ACTIVE'\n" +
                "        group by p.postIdx;\n" ;
        int selectUserPostsParam = userIdx;
        return this.jdbcTemplate.query(selectUserPostsQuery,
                (rs,rowNum) -> new GetPostsRes(
                        rs.getInt("postIdx"),
                        rs.getInt("userIdx"),
                        rs.getString("nickName"),
                        rs.getString("profileImgUrl"),
                        rs.getString("content"),
                        rs.getInt("postLikeCount"),
                        rs.getInt("commentCount"),
                        rs.getString("updatedAt"),
                        rs.getString("likeOrNot"),
                         getPostImgRes = this.jdbcTemplate.query(
                                         "SELECT pi.postImgUrlIdx,\n"+
                                         "            pi.imgUrl\n" +
                                         "        FROM PostImgUrl as pi\n" +
                                         "            join Post as p on p.postIdx = pi.postIdx\n" +
                                         "        WHERE pi.status = 'ACTIVE' and p.postIdx = ?;\n",
                        (rk,rownum) -> new GetPostImgRes(
                                rk.getInt("postImgUrlIdx"),
                                rk.getString("imgUrl"))
                                 ,rs.getInt("postIdx"))),selectUserPostsParam);



    }

    // 회원 확인
    public String checkUserStatus(String email){
        String checkUserStatusQuery = "select status from User where email = ? ";
        String checkUserStatusParams = email;
        return this.jdbcTemplate.queryForObject(checkUserStatusQuery,
                String.class,
                checkUserStatusParams);

    }

    // 게시물, 유저 확인
    public int checkUserPostExist(int userIdx, int postIdx){
        String checkUserPostQuery = "select exists(select postIdx from Post where postIdx = ? and userIdx=?) ";
        Object[]  checkUserPostParams = new Object[]{postIdx,userIdx};
        return this.jdbcTemplate.queryForObject(checkUserPostQuery,
                int.class,
                checkUserPostParams);

    }

    // 게시글 작성
    public int insertPost(int userIdx, PostPostReq postPostReq){
        String insertPostQuery =
                "        INSERT INTO Post(userIdx, content)\n" +
                "        VALUES (?, ?);";
        Object[] insertPostParams = new Object[]{userIdx,postPostReq.getContent()};
        this.jdbcTemplate.update(insertPostQuery, insertPostParams);

        String lastInserIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInserIdQuery,int.class);

    }

    // 게시글 이미지 작성
    public int insertPostImgs(int postIdx, PostImgsUrlReq postImgsUrlReq){
        String insertPostImgQuery =
                "        INSERT INTO PostImgUrl(postIdx, imgUrl)\n" +
                "        VALUES (?, ?);";
        Object[] insertPostImgParams = new Object[]{postIdx,postImgsUrlReq.getImgUrl()};
        this.jdbcTemplate.update(insertPostImgQuery, insertPostImgParams);

        String lastInserIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInserIdQuery,int.class);
    }

    // 게시글  수정
    public int updatePost(int postIdx,  PatchPostReq patchPostReq){
        String updatePostQuery = "UPDATE Post\n" +
                "        SET content = ?\n" +
                "        WHERE postIdx = ?" ;
        Object[] updatePostParams = new Object[]{patchPostReq.getContent(), postIdx};

        return this.jdbcTemplate.update(updatePostQuery,updatePostParams);
    }

    //게시글 삭제
    public int updatePostStatus(int postIdx){
        String deleteUserQuery = "UPDATE Post\n" +
                "        SET status = 'INACTIVE'\n" +
                "        WHERE postIdx = ? ";
        Object[] deleteUserParams = new Object[]{postIdx};

        return this.jdbcTemplate.update(deleteUserQuery,deleteUserParams);
    }
}
