package com.springmart.controller.api;

import com.springmart.entity.PromoCode;
import com.springmart.service.PromoCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/promo-codes")
@RequiredArgsConstructor
public class PromoCodeController {

    private final PromoCodeService promoCodeService;

    @PostMapping("/validate")
    public ResponseEntity<?> validatePromoCode(
            @RequestParam String code,
            @RequestParam BigDecimal orderAmount,
            @AuthenticationPrincipal OAuth2User principal) {
        
        try {
            Long userId = principal.getAttribute("id");
            PromoCode promoCode = promoCodeService.validatePromoCode(code, userId, orderAmount);
            BigDecimal discount = promoCodeService.calculateDiscount(promoCode, orderAmount);
            
            Map<String, Object> response = new HashMap<>();
            response.put("valid", true);
            response.put("code", promoCode.getCode());
            response.put("description", promoCode.getDescription());
            response.put("discountAmount", discount);
            response.put("discountType", promoCode.getDiscountType());
            response.put("discountValue", promoCode.getDiscountValue());
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("valid", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @GetMapping("/{code}")
    public ResponseEntity<PromoCode> getPromoCode(@PathVariable String code) {
        return ResponseEntity.ok(promoCodeService.getPromoCodeByCode(code));
    }
}
