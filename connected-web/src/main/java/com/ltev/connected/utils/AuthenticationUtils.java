package com.ltev.connected.utils;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuthenticationUtils {

    public static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    public static boolean isAuthenticated() {
        return getAuthentication() instanceof AnonymousAuthenticationToken == false;
    }

    public static Authentication checkAuthenticationOrThrow() {
        if (! isAuthenticated()) {
            throw new RuntimeException("not authenticated");
        }
        return getAuthentication();
    }

    public static String getUsername() {
        return getAuthentication().getName();
    }
}