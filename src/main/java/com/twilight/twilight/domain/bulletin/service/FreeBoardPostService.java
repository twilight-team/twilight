package com.twilight.twilight.domain.bulletin.service;

import com.twilight.twilight.domain.bulletin.dto.*;
import com.twilight.twilight.domain.bulletin.entity.FreeBoardPost;
import com.twilight.twilight.domain.bulletin.repository.FreeBoardPostQueryRepository;
import com.twilight.twilight.domain.bulletin.repository.FreeBoardPostRepository;
import com.twilight.twilight.domain.member.entity.Member;
import com.twilight.twilight.domain.member.type.Role;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FreeBoardPostService {

    private final FreeBoardPostRepository freeBoardPostRepository;
    private final FreeBoardPostQueryRepository freeBoardPostQueryRepository;
    private final StringRedisTemplate redisTemplate;
    private static int numberOfPostPerPage = 8; //페이지당 글 몇개씩 보여줄건지
    private static int numberOfReplyPerPage = 10; //일단 최대 10개의 최신글을 보여주자

    private static final String TOTAL_COUNT_KEY = "freeBoard:totalCount";

    public long getTotalPostCount() {
        String totalCountKey = redisTemplate.opsForValue().get(TOTAL_COUNT_KEY);
        if (totalCountKey != null) return Long.parseLong(totalCountKey);
        long count = freeBoardPostRepository.count();
        redisTemplate.opsForValue().set(TOTAL_COUNT_KEY, String.valueOf(count), Duration.ofMinutes(5)); // TTL 5분
        return count;
    }

    //일단 8개씩 준다
    public List<GetFreeBoardPostListDto> getFreeBoardPosts() {
        return freeBoardPostQueryRepository.findTopNByOrderByCreatedAtDesc(numberOfPostPerPage);
    }

    public GetFreeBoardPostDetailDto getFreeBoardPostDetail(Long postId) {
        return freeBoardPostRepository.findByFreeBoardPostId(postId)
                .map(GetFreeBoardPostDetailDto::fromEntity)
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다. id=" + postId));
    }

    public List<GetFreeBoardPostReplyDto> getFreeBoardPostReplies(Long postId) {
        return freeBoardPostQueryRepository.findTopNRepliesOrderByCreatedAtDesc(postId, numberOfReplyPerPage);
    }

    public void savePost(Member member, FreeBoardPostForm form ) {
        freeBoardPostRepository.save(
                FreeBoardPost.builder()
                        .member(member)
                        .title(form.getTitle())
                        .content(form.getContent())
                        .build()
        );
    }

    @Transactional
    public void editPost(Member member, Long postId, FreeBoardPostForm form) {
        FreeBoardPost post = freeBoardPostRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));
        if (!post.getMember().getMemberId().equals(member.getMemberId())) {
            throw new AccessDeniedException("본인 글만 수정할 수 있습니다.");
        }

        post.setTitle(form.getTitle());
        post.setContent(form.getContent());
    }

    public GetFreeBoardPostEditDto getEditablePost(Member member, Long postId) {
        FreeBoardPost post = freeBoardPostRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));
        if (!post.getMember().getMemberId().equals(member.getMemberId())) {
            throw new AccessDeniedException("본인 글만 수정할 수 있습니다.");
        }
        return GetFreeBoardPostEditDto.fromEntity(post);
    }

    @Transactional
    public void deletePost(Member member, Long postId) {
        FreeBoardPost post = freeBoardPostRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));
        if (!post.getMember().getMemberId().equals(member.getMemberId())
            || member.getRole().equals(Role.ROLE_ADMIN.toString())
        ) {
            throw new AccessDeniedException("본인 또는 관리자만 삭제 할 수 있습니다.");
        }

        freeBoardPostRepository.delete(post);
    }



}
