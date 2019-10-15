package com.liuyanzhao.sens.model.dto;

import com.liuyanzhao.sens.entity.Comment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 评论分页DTO
 * @author 言曌
 * @date 2019-10-12 11:26
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentPageDTO {

    private ListPage<Comment> commentListPage;

    private int[] rainbow;


}
