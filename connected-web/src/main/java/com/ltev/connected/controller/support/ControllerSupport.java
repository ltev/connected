package com.ltev.connected.controller.support;

import jakarta.servlet.http.HttpServletRequest;

public class ControllerSupport {

    public static String redirectToLastUrl(HttpServletRequest request) {
        return "redirect:" + request.getHeader("Referer");
    }
}