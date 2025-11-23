package com.springmart.service;

import com.springmart.dto.AddressDTO;
import com.springmart.dto.SavedCardDTO;
import com.springmart.entity.Address;
import com.springmart.entity.SavedCard;
import com.springmart.entity.User;
import com.springmart.exception.ResourceNotFoundException;
import com.springmart.repository.AddressRepository;
import com.springmart.repository.SavedCardRepository;
import com.springmart.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserProfileService {

    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final SavedCardRepository savedCardRepository;

    public List<AddressDTO> getUserAddresses(Long userId) {
        return addressRepository.findByUserId(userId).stream()
                .map(this::convertToAddressDTO)
                .collect(Collectors.toList());
    }

    public AddressDTO addAddress(Long userId, AddressDTO addressDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        Address address = Address.builder()
                .user(user)
                .street(addressDTO.getStreet())
                .city(addressDTO.getCity())
                .state(addressDTO.getState())
                .zipCode(addressDTO.getZipCode())
                .country(addressDTO.getCountry())
                .isDefault(addressDTO.isDefault())
                .build();

        if (address.getIsDefault()) {
            // Unset other default addresses
            addressRepository.findByUserId(userId).forEach(a -> {
                a.setIsDefault(false);
                addressRepository.save(a);
            });
        }

        return convertToAddressDTO(addressRepository.save(address));
    }

    public void deleteAddress(Long userId, Long addressId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "id", addressId));

        if (!address.getUser().getId().equals(userId)) {
            throw new RuntimeException("Access denied");
        }

        addressRepository.delete(address);
    }

    public List<SavedCardDTO> getUserCards(Long userId) {
        return savedCardRepository.findByUserId(userId).stream()
                .map(this::convertToCardDTO)
                .collect(Collectors.toList());
    }

    public SavedCardDTO addCard(Long userId, SavedCardDTO cardDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        SavedCard card = SavedCard.builder()
                .user(user)
                .last4(cardDTO.getLast4())
                .brand(cardDTO.getBrand())
                .expiryMonth(cardDTO.getExpiryMonth())
                .expiryYear(cardDTO.getExpiryYear())
                .holderName(cardDTO.getHolderName())
                .isDefault(cardDTO.isDefault())
                .build();

        if (card.getIsDefault()) {
            // Unset other default cards
            savedCardRepository.findByUserId(userId).forEach(c -> {
                c.setIsDefault(false);
                savedCardRepository.save(c);
            });
        }

        return convertToCardDTO(savedCardRepository.save(card));
    }

    public void deleteCard(Long userId, Long cardId) {
        SavedCard card = savedCardRepository.findById(cardId)
                .orElseThrow(() -> new ResourceNotFoundException("Card", "id", cardId));

        if (!card.getUser().getId().equals(userId)) {
            throw new RuntimeException("Access denied");
        }

        savedCardRepository.delete(card);
    }

    private AddressDTO convertToAddressDTO(Address address) {
        return AddressDTO.builder()
                .id(address.getId())
                .street(address.getStreet())
                .city(address.getCity())
                .state(address.getState())
                .zipCode(address.getZipCode())
                .country(address.getCountry())
                .isDefault(address.getIsDefault())
                .build();
    }

    private SavedCardDTO convertToCardDTO(SavedCard card) {
        return SavedCardDTO.builder()
                .id(card.getId())
                .last4(card.getLast4())
                .brand(card.getBrand())
                .expiryMonth(card.getExpiryMonth())
                .expiryYear(card.getExpiryYear())
                .holderName(card.getHolderName())
                .isDefault(card.getIsDefault())
                .build();
    }
}
