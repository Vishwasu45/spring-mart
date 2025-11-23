package com.springmart.config;

import com.springmart.security.CustomOAuth2User;
import com.springmart.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalControllerAdvice {

    private final CartService cartService;

    @ModelAttribute("cartItemCount")
    public Integer getCartItemCount(@AuthenticationPrincipal CustomOAuth2User currentUser) {
        if (currentUser == null) {
            return 0;
        }

        try {
            return cartService.getCartItemCount(currentUser.getId());
        } catch (Exception e) {
            return 0;
        }
    }
}
