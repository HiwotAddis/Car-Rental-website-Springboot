package com.example.carrentalsystem.service;

import com.example.carrentalsystem.model.Car;
import com.example.carrentalsystem.repository.CarRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CarService {
    private final CarRepository carRepository;

    public List<Car> findAllAvailable() {
        return carRepository.findAll();
    }
    

    public Car saveCar(Car car) {
        return carRepository.save(car);
    }
    public Car findById(Long id) {
        return carRepository.findById(id).orElseThrow(() -> new RuntimeException("Car not found with ID: " + id));
    }


    public Car updateCar(Long id, Car updatedCar) {
        return carRepository.findById(id).map(car -> {
            car.setMake(updatedCar.getMake());
            car.setModel(updatedCar.getModel());
            car.setYear(updatedCar.getYear());
            car.setPricePerDay(updatedCar.getPricePerDay());
            car.setAvailabilityStatus(updatedCar.getAvailabilityStatus());
//            car.setImagePath(updatedCar.getImagePath());
            if (updatedCar.getImagePath() != null) car.setImagePath(updatedCar.getImagePath());
            return carRepository.save(car);
        }).orElseThrow(() -> new RuntimeException("Car not found"));
    }

    public void deleteCar(Long id) {
        carRepository.deleteById(id);
    }
    
    // 🔹 New Search Method
    public List<Car> searchCars(String make, String model, Double pricePerDay) {
        return carRepository.findByMakeContainingIgnoreCaseAndModelContainingIgnoreCaseAndPricePerDayLessThanEqual(
                make != null ? make : "",
                model != null ? model : "",
                pricePerDay != null ? pricePerDay : Double.MAX_VALUE
        );
    }
}
