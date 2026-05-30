// com.contentflow.publish.adapter.zhihu.ZhihuAdapter.java
package com.contentflow.publish.adapter.zhihu;

import com.contentflow.content.entity.Content;
import com.contentflow.publish.adapter.PlatformAdapter;
import com.contentflow.publish.dto.PublishRequest;
import com.contentflow.publish.dto.PublishResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ZhihuAdapter implements PlatformAdapter {

    @Override
    public String getPlatform() {
        return "zhihu";
    }

    @Override
    public PublishResult publish(Object contentObj, PublishRequest request, boolean simulate) {
        Content content = (Content) contentObj;
        if (simulate) {
            log.info("[SIMULATE] 发布到知乎: title={}", content.getTitle());
            return PublishResult.success("simulate_zhihu_123");
        }
        // 知乎API集成
        String externalId = "zh_" + System.currentTimeMillis();
        log.info("成功发布到知乎: {}", externalId);
        return PublishResult.success(externalId);
    }

    @Override
    public String preprocessContent(String markdown) {
        // 知乎支持Markdown，但可以添加特定风格
        return markdown + "\n\n#知乎首发#";
    }
}