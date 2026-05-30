// com.contentflow.ai.service.AiRewriteService.java
package com.contentflow.ai.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AiRewriteService {

    @Value("${ai.openai.api-key:}")
    private String apiKey;

    public String rewrite(String content, String style) {
        if (apiKey == null || apiKey.isEmpty()) {
            log.warn("未配置OpenAI API Key，使用模拟重写");
            return simulateRewrite(content, style);
        }
        // 实际调用OpenAI API
        // ...
        return simulateRewrite(content, style);
    }

    private String simulateRewrite(String content, String style) {
        return content + "\n\n[AI重写] 按" + style + "风格优化后的内容";
    }
}