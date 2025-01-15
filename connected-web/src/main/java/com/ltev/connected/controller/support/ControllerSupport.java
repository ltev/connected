package com.ltev.connected.controller.support;

import jakarta.servlet.http.HttpServletRequest;

public class ControllerSupport {

    public static String getLastUrl(HttpServletRequest request) {
        return request.getHeader("Referer");
    }

    public static String redirectToLastUrl(HttpServletRequest request) {
        return "redirect:" + request.getHeader("Referer");
    }

    public static String redirectToLastUrlWithoutParameters(HttpServletRequest request) {
        String lastUrl = request.getHeader("Referer");
        int paramIdx = lastUrl.indexOf('?');
        return "redirect:" + (paramIdx == -1 ? lastUrl : lastUrl.substring(0, paramIdx));
    }
}