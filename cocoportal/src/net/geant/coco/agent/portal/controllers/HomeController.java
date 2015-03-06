package net.geant.coco.agent.portal.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController {
    @RequestMapping("/foo")
    public String showHome() {

        return "vpns";
    }
}
