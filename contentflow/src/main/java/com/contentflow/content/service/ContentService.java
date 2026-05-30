// com.contentflow.content.service.ContentService.java
package com.contentflow.content.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.contentflow.common.exception.BusinessException;
import com.contentflow.content.dto.ContentCreateRequest;
import com.contentflow.content.entity.Content;
import com.contentflow.content.mapper.ContentMapper;
import com.contentflow.publish.engine.AdaptEngine;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ContentService {

    private final ContentMapper contentMapper;
    private final AdaptEngine adaptEngine;

    public Content createContent(ContentCreateRequest request) {
        Long userId = getCurrentUserId();
        Content content = new Content();
        content.setUserId(userId);
        content.setTitle(request.getTitle());
        content.setContentMd(request.getContentMd());
        content.setSummary(request.getSummary());
        content.setCoverImage(request.getCoverImage());
        content.setStatus(request.getStatus() != null ? request.getStatus() : "DRAFT");
        // 转换为HTML
        String html = adaptEngine.markdownToHtml(request.getContentMd());
        content.setContentHtml(html);
        contentMapper.insert(content);
        return content;
    }

    public Page<Content> listUserContents(int page, int size) {
        Long userId = getCurrentUserId();
        Page<Content> pageParam = new Page<>(page, size);
        return contentMapper.selectPage(pageParam,
                new LambdaQueryWrapper<Content>()
                        .eq(Content::getUserId, userId)
                        .orderByDesc(Content::getCreatedAt));
    }

    public Content getContent(Long id) {
        Content content = contentMapper.selectById(id);
        if (content == null || !content.getUserId().equals(getCurrentUserId())) {
            throw new BusinessException("内容不存在或无权限");
        }
        return content;
    }

    private Long getCurrentUserId() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        // 实际应从redis或数据库查询用户ID，简化处理
        return 1L; // 演示
    }
}
