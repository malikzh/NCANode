package kz.ncanode.controller;

import kz.ncanode.NCANode;
import kz.ncanode.service.MaintenanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;

@Controller
@RequiredArgsConstructor
public class HomePageController {
    private final MaintenanceService maintenanceService;

    @Value("classpath:home.html")
    private Resource homePage;
    @RequestMapping(value = "/", produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String homePage() {
        return loadHtml()
            .replace(variable("VERSION"), maintenanceService.getNCANodeVersion())
            .replace(variable("BANNER"), NCANode.banner());
    }

    private String loadHtml() {
        try (Reader reader = new InputStreamReader(homePage.getInputStream(), StandardCharsets.UTF_8)) {
            return FileCopyUtils.copyToString(reader);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static String variable(String name) {
        return String.format("#{%s}", name);
    }
}
