package kz.ncanode;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.*;

@SpringBootApplication
@EnableScheduling
@EnableCaching
@EnableRetry
@Controller
@SpringBootApplication
public class NCANode extends SpringBootServletInitializer {

    @RequestMapping("/")
    @ResponseBody
    String home() {
        return "Hello World!";
    }

    public static void main(String[] args) {
        System.out.println(banner());
        SpringApplication.run(NCANode.class, args);
    }

    private static String banner() {
        return """
                 ____  _____   ______       _       ____  _____               __          ______  \s
                |_   \\|_   _|.' ___  |     / \\     |_   \\|_   _|             |  ]        / ____ `.\s
                  |   \\ | | / .'   \\_|    / _ \\      |   \\ | |   .--.    .--.| | .---.   `'  __) |\s
                  | |\\ \\| | | |          / ___ \\     | |\\ \\| | / .'`\\ \\/ /'`\\' |/ /__\\\\  _  |__ '.\s
                 _| |_\\   |_\\ `.___.'\\ _/ /   \\ \\_  _| |_\\   |_| \\__. || \\__/  || \\__., | \\____) |\s
                |_____|\\____|`.____ .'|____| |____||_____|\\____|'.__.'  '.__.;__]'.__.'  \\______.'\s
                """;
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(NCANode.class);
    }

}
