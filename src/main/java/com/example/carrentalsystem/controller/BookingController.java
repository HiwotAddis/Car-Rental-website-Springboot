package com.example.carrentalsystem.controller;

import com.example.carrentalsystem.dto.BookingRequest;
import com.example.carrentalsystem.model.Booking;
import com.example.carrentalsystem.service.BookingService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public Booking createBooking(@RequestBody BookingRequest bookingRequest) throws MessagingException {
        return bookingService.createBooking(
                bookingRequest.getCarId(),
                bookingRequest.getUserId(),
                bookingRequest.getStartDate(),
                bookingRequest.getEndDate(),
                bookingRequest.getTotalPrice() // Pass the total price from request
        );
    }

    @DeleteMapping("/{bookingId}")
    public String cancelBooking(@PathVariable Long bookingId) throws MessagingException {
        bookingService.cancelBooking(bookingId);
        return "Booking canceled successfully";
    }

    @GetMapping("/admin")
    public List<Booking> getAllBookings() {
        return bookingService.getAllBookings();
    }

    @GetMapping("/user/{userId}")
    public List<Booking> getUserBookings(@PathVariable Long userId) {
        return bookingService.getUserBookings(userId);
    }
}
