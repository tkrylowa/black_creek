package ru.spring.tkrylova.blackcreek.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
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
    public String registerUser(@ModelAttribute @Valid BlackCreekUser user, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "account/register";
        }
        BlackCreekUser savedUser = blackCreekUserService.saveUser(user);
        log.atInfo().log("New user with login {} was successfully created", savedUser.getLogin());
        return "redirect:/account/login";
    }
}
