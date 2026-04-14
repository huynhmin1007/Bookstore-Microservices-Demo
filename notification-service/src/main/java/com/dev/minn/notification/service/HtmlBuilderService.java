package com.dev.minn.notification.service;

import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.StringTemplateResolver;

import java.util.Map;

@Service
public class HtmlBuilderService {

    private final TemplateEngine templateEngine;

    // Yêu cầu Spring tiêm cái TemplateEngine có sẵn của nó vào đây
    public HtmlBuilderService(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;

        // "Dạy" thêm cho Engine này cách đọc HTML từ chuỗi String
        StringTemplateResolver stringTemplateResolver = new StringTemplateResolver();
        stringTemplateResolver.setCacheable(false);
        // Đặt Order = 1 để nó ưu tiên dịch nội dung từ String trước khi đi tìm file .html
        stringTemplateResolver.setOrder(1);

        this.templateEngine.addTemplateResolver(stringTemplateResolver);
    }

    public String buildHtml(String htmlContent, Map<String, Object> payload) {
        Context context = new Context();

        if (payload != null && !payload.isEmpty()) {
            context.setVariables(payload);
        }

        // Thực hiện nhào nặn (Sử dụng SpEL của Spring)
        return templateEngine.process(htmlContent, context);
    }
}