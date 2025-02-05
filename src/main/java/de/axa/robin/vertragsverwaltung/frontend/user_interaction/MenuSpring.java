package de.axa.robin.vertragsverwaltung.frontend.user_interaction;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MenuSpring {
    private int vsnr;

    public int getVsnr() {
        return vsnr;
    }

    public void setVsnr(int vsnr) {
        this.vsnr = vsnr;
    }

    @GetMapping("/")
    public String startWebsite() {
        return "index";
    }

    @GetMapping("/home")
    public String home() {
        return "home";
    }
}