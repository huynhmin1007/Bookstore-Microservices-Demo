package com.dev.minn.notification.service;

import com.dev.minn.common.exception.CodeException;
import com.dev.minn.notification.node.Template;
import com.dev.minn.notification.repository.TemplateRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class TemplateService {

    TemplateRepository templateRepository;

    public Template findTemplate(String templateCode) {
        return templateRepository.findByCode(templateCode)
                .orElseThrow(CodeException.RESOURCE_NOT_FOUND::throwException);
    }
}
