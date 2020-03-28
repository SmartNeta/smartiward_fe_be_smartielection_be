package com.mnt.sampark;

import org.springframework.context.annotation.Configuration;

import com.roncoo.shiro.freemarker.ShiroTags;

import freemarker.template.TemplateModelException;

@Configuration
public class FreemarkerConfig {
    public FreemarkerConfig(freemarker.template.Configuration configuration) throws TemplateModelException {
        configuration.setSharedVariable("shiro", new ShiroTags());
    }
}
