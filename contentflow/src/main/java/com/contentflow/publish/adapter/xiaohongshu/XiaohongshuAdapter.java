// com.contentflow.publish.adapter.xiaohongshu.XiaohongshuAdapter.java
package com.contentflow.publish.adapter.xiaohongshu;

import com.contentflow.content.entity.Content;
import com.contentflow.publish.adapter.PlatformAdapter;
import com.contentflow.publish.dto.PublishRequest;
import com.contentflow.publish.dto.PublishResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class XiaohongshuAdapter implements PlatformAdapter {

    @Override
    public String getPlatform() {
        return "xiaohongshu";
    }

    @Override
    public PublishResult publish(Object contentObj, PublishRequest request, boolean simulate) {
        Content content = (Content) contentObj;
        if (simulate) {
            log.info("[SIMULATE] 发布到小红书: title={}", content.getTitle());
            return PublishResult.success("simulate_xhs_123");
        }
        String externalId = "xhs_" + System.currentTimeMillis();
        log.info("成功发布到小红书: {}", externalId);
        return PublishResult.success(externalId);
    }

    @Override
    public String preprocessContent(String markdown) {
        // 小红书需要加emoji和话题标签
        return "✨ " + markdown + " ✨\n#内容创作 #自媒体";
    }
}
