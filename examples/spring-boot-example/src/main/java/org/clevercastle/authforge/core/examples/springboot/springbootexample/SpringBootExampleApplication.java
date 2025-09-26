package org.clevercastle.authforge.core.examples.springboot.springbootexample;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = {"org.clevercastle.authforge.core.model", "org.clevercastle.authforge.impl.postgres.entity"})
@EnableJpaRepositories(basePackages = {"org.clevercastle.authforge.impl.postgres.repository"})
@ComponentScan(basePackages = {"org.clevercastle.authforge"})
@Import({Beans.class, RepositoryBeans.class})
public class SpringBootExampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootExampleApplication.class, args);
    }

}
