package com.mnt.sampark.modules.mdm.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/open")
@CrossOrigin(origins = "*")
public class CustomerPortalController {

    @RequestMapping(value = "/customer/login")
    public String login() {
        return "customer/login";
    }

    @RequestMapping(value = "/customer/home")
    public String home() {
        return "customer/home";
    }

    @RequestMapping(value = "/customer/news")
    public String news() {
        return "customer/news";
    }

    @RequestMapping(value = "/customer/complaints")
    public String complaints() {
        return "customer/complaints";
    }

    @RequestMapping(value = "/customer/contact-us")
    public String contactUs() {
        return "customer/contact-us";
    }

    @RequestMapping(value = "/customer/notification")
    public String notification() {
        return "customer/notification";
    }

    @RequestMapping(value = "/customer/new-complaint")
    public String newComplaint() {
        return "customer/create_complaint";
    }
}
