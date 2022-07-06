package com.spring.ribborn.dto.responseDto;

import com.spring.ribborn.model.Content;
import com.spring.ribborn.model.Contents;
import com.spring.ribborn.model.Images;
import com.spring.ribborn.model.Post;
import com.spring.ribborn.repository.ContentsRepository;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class PostWriteResponseDto {
    private static ContentsRepository contentsRepository;
    @Builder
    public static class WriteMain {
        private Long id;
        private Contents image;
        private int likeCount;
        private int commentCount;
        private String nickname;
        private String title;
        private String category;

        public static WriteMain from(Post post) {
            Contents viewImage = contentsRepository.findTop1ByPostIdOrderByCreateAtDesc(post.getId());
            return WriteMain.builder()
                    .id(post.getId())
                    .image(viewImage)
                    .likeCount(post.getLikeCount())
                    .commentCount(post.getCommentCount())
                    .nickname(post.getUser().getNickname())
                    .title(post.getTitle())
                    .category(post.getCategory())
                    .build();
        }
    }

//    @Builder
//    public static class WriteDetail {
//        private Long id;
//        private String nickname;
//        private List<Images> images;
//        private String title;
//        private String category;
//        private List<Content> content;
//        private int likeCount;
//        private LocalDateTime createAt;
//        private LocalDateTime modifyAt;
//    }
}
