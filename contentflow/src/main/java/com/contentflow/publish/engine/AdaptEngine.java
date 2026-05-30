// com.contentflow.publish.engine.AdaptEngine.java
package com.contentflow.publish.engine;

import com.contentflow.ai.service.AiRewriteService;
import com.contentflow.content.entity.Content;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.MutableDataSet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AdaptEngine {

    private final Parser markdownParser;
    private final HtmlRenderer htmlRenderer;
    private final AiRewriteService aiRewriteService;

    public AdaptEngine() {
        MutableDataSet options = new MutableDataSet();
        this.markdownParser = Parser.builder(options).build();
        this.htmlRenderer = HtmlRenderer.builder(options).build();
    }

    public String markdownToHtml(String markdown) {
        Node document = markdownParser.parse(markdown);
        return htmlRenderer.render(document);
    }

    public String adaptForPlatform(String markdown, String platform) {
        // 平台特定风格适配，可扩展
        switch (platform) {
            case "xiaohongshu":
                return "✨ " + markdown + " ✨\n#内容创作";
            case "zhihu":
                return markdown + "\n\n---\n本文首发于知乎";
            case "wechat":
                // 微信公众号可能需要处理特殊标签
                return markdown;
            default:
                return markdown;
        }
    }

    public String rewriteWithAi(String content, String style) {
        return aiRewriteService.rewrite(content, style);
    }
}