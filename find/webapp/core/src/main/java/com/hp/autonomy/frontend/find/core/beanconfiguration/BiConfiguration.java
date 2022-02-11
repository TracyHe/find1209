/*
 * Copyright 2014-2018 Micro Focus International plc.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.frontend.find.core.beanconfiguration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@Import({
        DataSourceAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class
})
@EnableJpaAuditing
@EnableJpaRepositories(basePackages = "com.hp.autonomy.frontend.find")
@EnableTransactionManagement
@ConditionalOnExpression(BiConfiguration.BI_PROPERTY_SPEL)
public class BiConfiguration {
    public final static String BI_PROPERTY = "idol.find.enableBi";
    public final static String BI_PROPERTY_HP = "hp.find.enableBi";
    public final static String BI_PROPERTY_SPEL = "${" + BI_PROPERTY + ":${" + BI_PROPERTY_HP + ":true}}";
}
