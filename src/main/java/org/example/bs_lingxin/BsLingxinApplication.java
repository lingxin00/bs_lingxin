package org.example.bs_lingxin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class BsLingxinApplication {

    public static void main(String[] args) {
        SpringApplication.run(BsLingxinApplication.class, args);
    }

}
