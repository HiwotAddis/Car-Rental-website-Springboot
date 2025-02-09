package com.example.carrentalsystem.controller;

import com.example.carrentalsystem.model.Car;
import com.example.carrentalsystem.service.CarService;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cars")
public class CarController {
    private final CarService carService;

    private final String UPLOAD_DIR = "uploads/";

    @GetMapping
    public List<Car> getAvailableCars() {
        List<Car> cars = carService.findAllAvailable();
        String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
        cars.forEach(car -> car.setImagePath(baseUrl + car.getImagePath()));
        return cars;
    }

    @PostMapping
    public Car addCar(@RequestParam("make") String make,
                      @RequestParam("model") String model,
                      @RequestParam("availabilityStatus") String availabilityStatus,
                      @RequestParam("year") int year,
                      @RequestParam("pricePerDay") double pricePerDay,
                      @RequestParam("file") MultipartFile file) throws IOException {
        String imagePath = uploadImage(file);
        Car car = new Car();
        car.setMake(make);
        car.setModel(model);
        car.setAvailabilityStatus(availabilityStatus);
        car.setYear(year);
        car.setPricePerDay(pricePerDay);
        car.setImagePath(imagePath);
        return carService.saveCar(car);
    }
    
    @GetMapping("/detail")
    public Car getCarById(@RequestParam("id") Long id) {
        Car car = carService.findById(id);
        String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
        car.setImagePath(baseUrl + car.getImagePath()); // Append the base URL to the image path
        return car;
    }


    @PutMapping("/{id}")
    public Car updateCar(@PathVariable Long id,
                         @RequestParam("make") String make,
                         @RequestParam("model") String model,
                         @RequestParam("availabilityStatus") String availabilityStatus,
                         @RequestParam("year") int year,
                         @RequestParam("pricePerDay") double pricePerDay,
                         @RequestParam(value = "file", required = false) MultipartFile file) throws IOException {
        Car updatedCar = new Car();
        updatedCar.setMake(make);
        updatedCar.setModel(model);
        updatedCar.setAvailabilityStatus(availabilityStatus);
        updatedCar.setYear(year);
        updatedCar.setPricePerDay(pricePerDay);

        if (file != null) {
            String imagePath = uploadImage(file);
            updatedCar.setImagePath(imagePath);
        }

        return carService.updateCar(id, updatedCar);
    }

    @DeleteMapping("/{id}")
    public void deleteCar(@PathVariable Long id) {
        carService.deleteCar(id);
    }
    
    @GetMapping("/search")
    public List<Car> searchCars(
            @RequestParam(required = false) String make,
            @RequestParam(required = false) String model,
            @RequestParam(required = false) Double pricePerDay) {
        return carService.searchCars(make, model, pricePerDay);
    }

    private String uploadImage(MultipartFile file) throws IOException {
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        Path filePath = Paths.get(UPLOAD_DIR + fileName);
        Files.createDirectories(filePath.getParent());
        Files.write(filePath, file.getBytes());
        return "/" + UPLOAD_DIR + fileName;
    }
}
