package com.example.carrentalsystem.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class BookingRequest {
    private Long carId;
    private Long userId;
    private LocalDate startDate;
    private LocalDate endDate;
    private Double totalPrice; // Allow frontend to send the calculated price
}
