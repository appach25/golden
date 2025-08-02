package com.golden.system.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.WebAttributes;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String login(Model model, HttpServletRequest request) {
        // Check for authentication error
        AuthenticationException exception = (AuthenticationException) request.getSession()
                .getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
        if (exception != null) {
            model.addAttribute("error", "Invalid username or password");
            request.getSession().removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
        }
        return "login";
    }
}
