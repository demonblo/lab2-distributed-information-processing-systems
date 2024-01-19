package ru.bmstu.reservationapp.service;


public interface TokenService {
    boolean validateToken(String token);

    String getUsername(String token);
}
