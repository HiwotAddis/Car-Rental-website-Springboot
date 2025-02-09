package com.example.carrentalsystem.repository;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;


import com.example.carrentalsystem.model.Car;
import org.springframework.stereotype.Repository;

@Repository 
public interface CarRepository extends JpaRepository<Car, Long> {
	
	List<Car> findByMakeContainingIgnoreCaseAndModelContainingIgnoreCaseAndPricePerDayLessThanEqual(
            String make, String model, double pricePerDay);
}