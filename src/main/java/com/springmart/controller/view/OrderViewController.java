package com.springmart.controller.view;

import com.springmart.security.CustomOAuth2User;
import com.springmart.service.OrderService;
import com.springmart.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderViewController {

    private final OrderService orderService;
    private final UserService userService;

    @GetMapping
    public String myOrders(
            @AuthenticationPrincipal CustomOAuth2User currentUser,
            @PageableDefault(size = 10) Pageable pageable,
            Model model) {

        if (currentUser == null) {
            return "redirect:/login";
        }

        Long userId = currentUser.getId();
        model.addAttribute("orders", orderService.getUserOrders(userId, pageable));
        model.addAttribute("user", userService.getUserById(userId));
        return "orders";
    }

    @GetMapping("/{id}")
    public String orderDetail(@PathVariable Long id, Model model) {
        model.addAttribute("order", orderService.getOrderById(id));
        return "order-detail";
    }
}

