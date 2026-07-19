package com.example.ecommerce_fashionformen.services;

public interface MailService {
    void sendEmail(String to, String subject, String body, boolean isHtml);
}
