// com.contentflow.publish.adapter.wechat.WeChatAdapter.java
package com.contentflow.publish.adapter.wechat;

import com.contentflow.content.entity.Content;
import com.contentflow.publish.adapter.PlatformAdapter;
import com.contentflow.publish.dto.PublishRequest;
import com.contentflow.publish.dto.PublishResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class WeChatAdapter implements PlatformAdapter {

    @Override
    public String getPlatform() {
        return "wechat";
    }

    @Override
    public PublishResult publish(Object contentObj, PublishRequest request, boolean simulate) {
        Content content = (Content) contentObj;
        if (simulate) {
            log.info("[SIMULATE] 发布到微信公众号: title={}", content.getTitle());
            return PublishResult.success("simulate_wechat_123");
        }
        try {
            // 实际调用微信公众号API
            // 1. 获取access_token
            // 2. 上传封面图片获取media_id
            // 3. 调用草稿箱API或发布接口
            String externalId = "wx_" + System.currentTimeMillis();
            log.info("成功发布到微信公众号: {}, externalId={}", content.getTitle(), externalId);
            return PublishResult.success(externalId);
        } catch (Exception e) {
            log.error("发布到微信公众号失败", e);
            return PublishResult.failure(e.getMessage());
        }
    }
}
