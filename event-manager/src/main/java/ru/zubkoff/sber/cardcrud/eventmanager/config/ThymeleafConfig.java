package ru.zubkoff.sber.cardcrud.eventmanager.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

@Configuration
public class ThymeleafConfig {

  @Bean
  public SpringTemplateEngine htmlTemplateEngine() {
    SpringTemplateEngine engine = new SpringTemplateEngine();
    engine.setTemplateResolver(templateResolver());
    return engine;
  }

  @Bean
  public ClassLoaderTemplateResolver  templateResolver() {
    ClassLoaderTemplateResolver  templateResolver = new ClassLoaderTemplateResolver ();
    templateResolver.setPrefix("templates/");
    templateResolver.setSuffix(".html");
    templateResolver.setTemplateMode("HTML");
    return templateResolver;
  }
}
