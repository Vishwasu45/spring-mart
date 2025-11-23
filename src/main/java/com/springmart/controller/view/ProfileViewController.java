package com.springmart.controller.view;

import com.springmart.security.CustomOAuth2User;

import com.springmart.service.UserProfileService;
import com.springmart.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileViewController {

    private final UserService userService;
    private final UserProfileService userProfileService;

    @GetMapping
    public String profile(@AuthenticationPrincipal CustomOAuth2User currentUser, Model model) {
        if (currentUser == null) {
            return "redirect:/login";
        }

        Long userId = currentUser.getId();
        model.addAttribute("user", userService.getUserById(userId));
        model.addAttribute("addresses", userProfileService.getUserAddresses(userId));
        model.addAttribute("cards", userProfileService.getUserCards(userId));
        // We might want to show recent orders on the profile page too
        // model.addAttribute("recentOrders",
        // orderService.getUserOrders(currentUser.getId(), PageRequest.of(0, 5)));

        return "profile";
    }
}
