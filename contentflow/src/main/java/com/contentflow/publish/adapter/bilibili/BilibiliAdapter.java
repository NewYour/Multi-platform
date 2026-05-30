// com.contentflow.publish.adapter.bilibili.BilibiliAdapter.java
package com.contentflow.publish.adapter.bilibili;

import com.contentflow.content.entity.Content;
import com.contentflow.publish.adapter.PlatformAdapter;
import com.contentflow.publish.dto.PublishRequest;
import com.contentflow.publish.dto.PublishResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class BilibiliAdapter implements PlatformAdapter {

    @Override
    public String getPlatform() {
        return "bilibili";
    }

    @Override
    public PublishResult publish(Object contentObj, PublishRequest request, boolean simulate) {
        Content content = (Content) contentObj;
        if (simulate) {
            log.info("[SIMULATE] 发布到B站专栏: title={}", content.getTitle());
            return PublishResult.success("simulate_bili_123");
        }
        String externalId = "bili_" + System.currentTimeMillis();
        log.info("成功发布到B站: {}", externalId);
        return PublishResult.success(externalId);
    }
}