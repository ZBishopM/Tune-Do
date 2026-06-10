package com.tunedo.tunedo.controllers;

import java.security.Principal;
import java.time.DateTimeException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.tunedo.tunedo.models.User;
import com.tunedo.tunedo.services.UserService;
import com.tunedo.tunedo.validators.UserValidator;

import jakarta.validation.Valid;

@Controller
@RequestMapping("")
public class UserController {
    private final UserService userService;
    private final UserValidator userValidator;

    public UserController(UserService userService, UserValidator userValidator) {
        this.userService = userService;
        this.userValidator = userValidator;
    }

    @InitBinder
    private void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(userValidator);
    }
    
    @GetMapping("/login")
    public String login(
        @ModelAttribute("user") User user,
        Principal principal,
        @RequestParam(value="error", required=false) String error,
        @RequestParam(value="logout", required=false) String logout,
        @RequestParam(value="registered", required=false) String registered,
        Model model
    ) {
        if(principal!=null){
            if(userService.existsByEmail(principal.getName()))            
                return "redirect:/home";
        }
        if(error != null) {
            model.addAttribute("errorMessage", "Usuario o contraseña incorrectos."+error);
        }
        if(logout != null) {
            model.addAttribute("logoutMessage", "Se cerró la sesión");
        }
        if(registered!=null){
            model.addAttribute("registered", "Se ha registrado, inicie sesión para continuar");
        }
        return "login.jsp";
    }

    @PostMapping("/register")
    public String save(
        @Valid @ModelAttribute("user") User user,
        BindingResult result,
        @RequestParam(value="day", required=false) Integer day,
        @RequestParam(value="month", required=false) Integer month,
        @RequestParam(value="year", required=false) Integer year
    ) {
        if (user == null) {
            return "registration.jsp";
        }
        if (result.hasErrors()) {
            return "registration.jsp";
        }
        if (day != null && month != null && year != null) {
        try {
            // Create a LocalDate object
            LocalDate date = LocalDate.of(year, month, day);
            
            // Convert LocalDate to Instant (assuming start of day in system default time zone)
            Instant instant = date.atStartOfDay(ZoneId.systemDefault()).toInstant();
            
            user.setBornDate(instant);
        } catch (DateTimeException e) {
            // Handle invalid date
            result.rejectValue("bornDate", "error.bornDate", "Invalid date");
            return "registration.jsp";
        }
    } else {
        // Handle case where date is not provided
        result.rejectValue("bornDate", "error.bornDate", "Date is required");
        return "registration.jsp";
    }
        userService.save(user);
        return "redirect:/login/?registered";
    }

    @GetMapping("/register")
    public String registerForm(
        @ModelAttribute("user") User user,
        BindingResult result) {
        return "registration.jsp";
    }
}
