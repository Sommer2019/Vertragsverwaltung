package de.axa.robin.vertragsverwaltung.user_interaction;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

//ToDo: keep Menu Opened after failed/reload
//ToDO: Frontend Check

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
}