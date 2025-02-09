package com.example.carrentalsystem.service;

import com.example.carrentalsystem.model.Booking;
import com.example.carrentalsystem.model.Car;
import com.example.carrentalsystem.model.User;
import com.example.carrentalsystem.repository.BookingRepository;
import com.example.carrentalsystem.repository.CarRepository;
import com.example.carrentalsystem.repository.UserRepository;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final CarRepository carRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    public Booking createBooking(Long carId, Long userId, LocalDate startDate, LocalDate endDate, Double totalPrice) throws MessagingException {
        // Validate car availability
        Optional<Car> carOptional = carRepository.findById(carId);
        if (carOptional.isEmpty()) {
            throw new RuntimeException("Car not found");
        }
        Car car = carOptional.get();
        if (!"Available".equals(car.getAvailabilityStatus())) {
            throw new RuntimeException("Car is not available for booking");
        }

        // Validate user
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new RuntimeException("User not found");
        }
        User user = userOptional.get();

        // Calculate total price if not provided
        if (totalPrice == null) {
            long days = java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate);
            totalPrice = days * car.getPricePerDay();
        }

        // Create booking
        Booking booking = new Booking();
        booking.setCar(car);
        booking.setUser(user);
        booking.setStartDate(startDate);
        booking.setEndDate(endDate);
        booking.setTotalPrice(totalPrice);
        booking.setStatus("Confirmed");

        // Update car availability
        car.setAvailabilityStatus("Unavailable");
        carRepository.save(car);

        // Save booking
        Booking savedBooking = bookingRepository.save(booking);

        // Send email
        emailService.sendBookingEmail(user.getEmail(), savedBooking);

        return savedBooking;
    }

    public void cancelBooking(Long bookingId) throws MessagingException {
        Optional<Booking> bookingOptional = bookingRepository.findById(bookingId);
        if (bookingOptional.isEmpty()) {
            throw new RuntimeException("Booking not found");
        }

        Booking booking = bookingOptional.get();
        Car car = booking.getCar();

        // Set car availability back to available
        car.setAvailabilityStatus("Available");
        carRepository.save(car);

        // Remove booking
        bookingRepository.delete(booking);

        // Send cancellation email
        emailService.sendCancellationEmail(booking.getUser().getEmail(), booking);
    }

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    public List<Booking> getUserBookings(Long userId) {
        return bookingRepository.findByUserId(userId);
    }
}
