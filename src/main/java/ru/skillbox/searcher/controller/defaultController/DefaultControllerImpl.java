package ru.skillbox.searcher.controller.defaultController;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DefaultControllerImpl implements DefaultController {


    @GetMapping("/admin")
    public String callDefault() {
        return "index";
    }
}
