package com.springmart.service;

import com.springmart.entity.Newsletter;
import com.springmart.repository.NewsletterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class NewsletterService {

    private final NewsletterRepository newsletterRepository;

    @Transactional
    public Newsletter subscribe(String email) {
        if (newsletterRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already subscribed to newsletter");
        }

        Newsletter newsletter = Newsletter.builder()
                .email(email)
                .isActive(true)
                .build();

        Newsletter saved = newsletterRepository.save(newsletter);
        log.info("New newsletter subscription: {}", email);
        return saved;
    }

    public boolean isSubscribed(String email) {
        return newsletterRepository.existsByEmail(email);
    }

    @Transactional
    public void unsubscribe(String email) {
        newsletterRepository.findByEmail(email).ifPresent(newsletter -> {
            newsletter.setIsActive(false);
            newsletterRepository.save(newsletter);
            log.info("Newsletter unsubscribed: {}", email);
        });
    }
}

