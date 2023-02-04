package com.hy.project.demo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * @author rick.wl
 * @date 2021/11/04
 */
@Configuration
public class TransactionTemplateConfig {
    @Autowired
    private PlatformTransactionManager transactionManager;

    @Bean(name = "transactionTemplate")
    public TransactionTemplate defaultTransactionTemplate() {
        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
        transactionTemplate.setTimeout(3);
        return transactionTemplate;
    }

    @Bean(name = "transactionTemplateNoPropagation")
    public TransactionTemplate transactionTemplateNoPropagation() {
        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
        transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_NOT_SUPPORTED);
        return transactionTemplate;
    }
}
