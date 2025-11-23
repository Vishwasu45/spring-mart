package com.springmart.controller.api;

import com.springmart.dto.AddressDTO;
import com.springmart.dto.SavedCardDTO;
import com.springmart.security.CustomOAuth2User;
import com.springmart.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserProfileService userProfileService;

    @GetMapping("/addresses")
    public ResponseEntity<List<AddressDTO>> getAddresses(@AuthenticationPrincipal CustomOAuth2User currentUser) {
        return ResponseEntity.ok(userProfileService.getUserAddresses(currentUser.getId()));
    }

    @PostMapping("/addresses")
    public ResponseEntity<AddressDTO> addAddress(
            @AuthenticationPrincipal CustomOAuth2User currentUser,
            @RequestBody AddressDTO addressDTO) {
        return ResponseEntity.ok(userProfileService.addAddress(currentUser.getId(), addressDTO));
    }

    @DeleteMapping("/addresses/{id}")
    public ResponseEntity<Void> deleteAddress(
            @AuthenticationPrincipal CustomOAuth2User currentUser,
            @PathVariable Long id) {
        userProfileService.deleteAddress(currentUser.getId(), id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/cards")
    public ResponseEntity<List<SavedCardDTO>> getCards(@AuthenticationPrincipal CustomOAuth2User currentUser) {
        return ResponseEntity.ok(userProfileService.getUserCards(currentUser.getId()));
    }

    @PostMapping("/cards")
    public ResponseEntity<SavedCardDTO> addCard(
            @AuthenticationPrincipal CustomOAuth2User currentUser,
            @RequestBody SavedCardDTO cardDTO) {
        return ResponseEntity.ok(userProfileService.addCard(currentUser.getId(), cardDTO));
    }

    @DeleteMapping("/cards/{id}")
    public ResponseEntity<Void> deleteCard(
            @AuthenticationPrincipal CustomOAuth2User currentUser,
            @PathVariable Long id) {
        userProfileService.deleteCard(currentUser.getId(), id);
        return ResponseEntity.ok().build();
    }
}
