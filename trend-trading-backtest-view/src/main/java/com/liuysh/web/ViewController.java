package com.liuysh.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author Liuysh
 * @date 2022/6/17 15:28
 * @Description:
 */
@Controller
public class ViewController {
    @GetMapping("/")
    public String view() {
        return "view";
    }
}
