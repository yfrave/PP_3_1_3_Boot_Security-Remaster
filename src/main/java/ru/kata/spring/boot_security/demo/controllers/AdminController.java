package ru.kata.spring.boot_security.demo.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.models.User;
import ru.kata.spring.boot_security.demo.servises.RoleService;
import ru.kata.spring.boot_security.demo.servises.UserService;

import javax.validation.Valid;

@Controller
@RequestMapping("/")
@PreAuthorize("hasAuthority('ADMIN')")
public class AdminController {
    private final UserService userService;
    private final RoleService roleService;

    @Autowired
    public AdminController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }


    @GetMapping("/admin")
    public String showUsers(ModelMap model) {

        model.addAttribute("user", userService.listUsers());

        return "/admin/index";
    }

    @GetMapping("/admin/{id}")
    public String show(@PathVariable Long id, ModelMap model) {
        model.addAttribute("user", userService.getUser(id));
        return "/admin/show";
    }

    @GetMapping("/admin/new")
    public String newUsers(Model model) {
        model.addAttribute("user", new User());
        return "/admin/new";
    }

    @GetMapping("/admin/{id}/edit")
    public String edit(ModelMap model, @PathVariable("id") Long id) {
        model.addAttribute("user", userService.getUser(id));
        return "/admin/edit";
    }

    @PostMapping("/admin")
    public String saveUsers(@ModelAttribute("user") @Valid User user, BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            return "/admin/new";
        user.setPassword(user.getPassword());
        userService.saveUser(user);
        return "/admin/index";
    }

    @PatchMapping("/admin/{id}")
    public String updateUsers(@ModelAttribute("user") @Valid User user, BindingResult bindingResult, @PathVariable("id") Long id) {
        user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
        if (bindingResult.hasErrors())
            return "/admin/edit";
        userService.update(id, user);
        return "redirect:/admin";
    }

    @DeleteMapping("/admin/{id}")
    public String delete(@PathVariable("id") Long id) {
        userService.removeUser(id);
        return "redirect:/admin";
    }

}
