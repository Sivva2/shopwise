package com.shopwise.repository;

import com.shopwise.entity.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceRepository extends JpaRepository<Service, Long> {

    List<Service> findByActiveTrue();

    List<Service> findByNameContainingIgnoreCase(String name);
}
