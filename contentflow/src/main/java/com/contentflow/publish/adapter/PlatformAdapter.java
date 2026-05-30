// com.contentflow.publish.adapter.PlatformAdapter.java
package com.contentflow.publish.adapter;

import com.contentflow.publish.dto.PublishRequest;
import com.contentflow.publish.dto.PublishResult;

public interface PlatformAdapter {
    /**
     * 获取平台名称
     */
    String getPlatform();

    /**
     * 发布内容
     * @param content 内容实体
     * @param request 发布请求
     * @param simulate 是否模拟发布
     * @return 发布结果
     */
    PublishResult publish(Object content, PublishRequest request, boolean simulate);

    /**
     * 平台特定内容预处理
     */
    default String preprocessContent(String markdown) {
        return markdown;
    }
}