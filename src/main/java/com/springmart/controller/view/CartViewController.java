package com.springmart.controller.view;

import com.springmart.security.CustomOAuth2User;
import com.springmart.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartViewController {

    private final CartService cartService;
    private final com.springmart.service.UserProfileService userProfileService;

    @GetMapping
    public String viewCart(@AuthenticationPrincipal CustomOAuth2User currentUser, Model model) {
        if (currentUser == null) {
            return "redirect:/login";
        }

        model.addAttribute("cartItems", cartService.getCartItems(currentUser.getId()));
        model.addAttribute("cartTotal", cartService.getCartTotal(currentUser.getId()));
        return "cart";
    }

    @PostMapping("/add")
    public String addToCart(@AuthenticationPrincipal CustomOAuth2User currentUser,
            @RequestParam Long productId,
            @RequestParam(defaultValue = "1") Integer quantity,
            RedirectAttributes redirectAttributes) {
        if (currentUser == null) {
            return "redirect:/login";
        }

        try {
            cartService.addToCart(currentUser.getId(), productId, quantity);
            redirectAttributes.addFlashAttribute("successMessage", "Item added to cart successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to add item to cart: " + e.getMessage());
        }

        return "redirect:/cart";
    }

    @GetMapping("/checkout")
    public String checkout(@AuthenticationPrincipal CustomOAuth2User currentUser, Model model) {
        if (currentUser == null) {
            return "redirect:/login";
        }

        Long userId = currentUser.getId();
        model.addAttribute("cartItems", cartService.getCartItems(userId));
        model.addAttribute("cartTotal", cartService.getCartTotal(userId));
        model.addAttribute("savedAddresses", userProfileService.getUserAddresses(userId));
        model.addAttribute("savedCards", userProfileService.getUserCards(userId));
        return "checkout";
    }
}
