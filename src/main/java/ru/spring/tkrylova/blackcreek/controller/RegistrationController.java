package ru.spring.tkrylova.blackcreek.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.spring.tkrylova.blackcreek.entity.BlackCreekUser;
import ru.spring.tkrylova.blackcreek.servce.BlackCreekUserService;

@Slf4j
@Controller
@RequestMapping("/register")
public class RegistrationController {
    private final BlackCreekUserService blackCreekUserService;

    public RegistrationController(BlackCreekUserService blackCreekUserService) {
        this.blackCreekUserService = blackCreekUserService;
    }

    @GetMapping
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new BlackCreekUser());
        return "account/register";
    }

    @PostMapping
    public String registerUser(@Valid BlackCreekUser user, BindingResult result, Model model) {
        model.addAttribute("user", new BlackCreekUser());
        if (blackCreekUserService.isLoginTaken(user.getLogin())) {
            model.addAttribute("error", "Login is already taken.");
            return "account/register";
        }
        if (!user.getPassword().equals(user.getConfirmPassword())) {
            model.addAttribute("error", "Passwords do not match.");
            return "account/register";
        }
        if (result.hasErrors()) {
            log.error("Error occurred! {}", result.getAllErrors());
            return "account/register";
        }
        BlackCreekUser savedUser = blackCreekUserService.saveUser(user);
        log.info("New user with login {} was successfully created", savedUser.getLogin());
        model.addAttribute("message", "Registration successful.");
        return "redirect:/login";
    }
}
