package com.contentflow.publish.engine;

import com.contentflow.ai.service.AiRewriteService;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.data.MutableDataSet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor  // 生成包含 final 字段的构造器
@Slf4j
public class AdaptEngine {

    private final AiRewriteService aiRewriteService;

    // 这两个字段不需要注入，直接初始化即可
    private final Parser markdownParser = Parser.builder(new MutableDataSet()).build();
    private final HtmlRenderer htmlRenderer = HtmlRenderer.builder(new MutableDataSet()).build();

    public String markdownToHtml(String markdown) {
        return htmlRenderer.render(markdownParser.parse(markdown));
    }

    public String adaptForPlatform(String markdown, String platform) {
        switch (platform) {
            case "xiaohongshu":
                return "✨ " + markdown + " ✨\n#内容创作";
            case "zhihu":
                return markdown + "\n\n---\n本文首发于知乎";
            case "wechat":
                return markdown;
            default:
                return markdown;
        }
    }

    public String rewriteWithAi(String content, String style) {
        if (aiRewriteService == null) {
            log.warn("AiRewriteService 未配置，使用模拟重写");
            return content + "\n\n[AI重写模拟] " + style + "风格";
        }
        return aiRewriteService.rewrite(content, style);
    }
}