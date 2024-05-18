package org.cotato.csquiz;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CsquizApplication {

    public static void main(String[] args) {
        SpringApplication.run(CsquizApplication.class, args);
    }

}
