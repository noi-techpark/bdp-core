package it.bz.idm.bdp.ninja.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import springfox.documentation.annotations.ApiIgnore;

@ApiIgnore
@Controller
public class SwaggerController {

    @RequestMapping("/")
    public String swaggerUi() {
        return "redirect:/swagger-ui.html";
    }
}