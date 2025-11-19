package com.example.demo.controller;


import com.example.demo.dto.AuthResponse;
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.service.AuthService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Set;

@Controller
@RequestMapping("/auth")
public class AuthWebController {

    private final AuthService authService;

    public AuthWebController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/login")
    public String mostrarLogin(Model model) {
        model.addAttribute("loginRequest", new LoginRequest("",""));
        return "auth/login";
    }

    @PostMapping("/login")
    public String login(
            @Valid @ModelAttribute("loginRequest") LoginRequest request,
            BindingResult result,
            Model model,
            HttpSession session,
            RedirectAttributes flash) {

        if (result.hasErrors()) {
            return "auth/login";
        }

        try {
            AuthResponse response = authService.login(request);
            session.setAttribute("token", response.token());
            session.setAttribute("nombreUsuario", response.nombre());
            session.setAttribute("roles", response.roles());

            flash.addFlashAttribute("success", "¡Bienvenido, " + response.nombre() + "!");
            return "redirect:/home";

        } catch (Exception e) {
            model.addAttribute("error", "Usuario o contraseña incorrectos");
            return "auth/login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes flash) {
        session.invalidate();  // Borra token, nombre, roles
        flash.addFlashAttribute("success", "¡Sesión cerrada correctamente!");
        return "redirect:/auth/login";
    }

    @GetMapping("/register")
    public String mostrarRegistro(Model model) {
        model.addAttribute("registerRequest", new RegisterRequest("", "", "", Set.of("VENDEDOR")));
        return "auth/register";
    }

    @PostMapping("/register")
    public String registrar(
            @Valid @ModelAttribute("registerRequest") RegisterRequest request,
            BindingResult result,
            Model model,
            HttpSession session,
            RedirectAttributes flash) {

        if (result.hasErrors()) {
            return "auth/register";
        }

        try {
            AuthResponse response = authService.register(request);
            session.setAttribute("token", response.token());
            session.setAttribute("nombreUsuario", response.nombre());
            session.setAttribute("roles", response.roles());

            flash.addFlashAttribute("success", "¡Registro exitoso! Bienvenido " + response.nombre());
            return "redirect:/home";

        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "auth/register";
        }
    }
}