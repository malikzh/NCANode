package kz.ncanode;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class NCANode {
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
}
