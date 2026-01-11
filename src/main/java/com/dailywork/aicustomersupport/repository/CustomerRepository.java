package com.dailywork.aicustomersupport.repository;

import com.dailywork.aicustomersupport.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer,Long>{
    Optional<Customer> findByEmailAddressAndPhoneNumber(String emailId, String phoneNumber);
}
