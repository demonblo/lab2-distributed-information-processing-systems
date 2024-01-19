package ru.bmstu.loyaltyapp.service;


public interface TokenService {
    boolean validateToken(String token);

    String getUsername(String token);
}
