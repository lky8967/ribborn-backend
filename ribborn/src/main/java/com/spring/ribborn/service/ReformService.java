package com.spring.ribborn.service;

import com.spring.ribborn.dto.responseDto.ReformResponseDto;
import com.spring.ribborn.model.Post;
import com.spring.ribborn.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReformService {
    private final PostRepository postRepository;

    // 리폼견적 목록페이지 조회
    @Transactional
    public Page<Post> getReforms(Pageable pageable) {
        return postRepository.findAll(pageable);
    }

    // 리폼견적 상세페이지 조회
    @Transactional
    public ReformResponseDto.ReformDetail getDetail(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new NullPointerException("게시글이 존재하지 않습니다.")
        );

        ReformResponseDto.ReformDetail detailDto = ReformResponseDto.ReformDetail.builder()
                .id(post.getId())
                .nickname(post.getUser().getNickname())
                .images(post.getImages())
                .title(post.getTitle())
                .category(post.getCategory())
                .content(post.getContent())
                .region(post.getRegion())
                .process(post.getProcess())
                .createAt(post.getCreateAt())
                .modifyAt(post.getModifyAt())
                .build();
        return detailDto;
    }
}
