package com.dailywork.aicustomersupport.repository;

import com.dailywork.aicustomersupport.model.Customer;
import com.dailywork.aicustomersupport.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<Customer,Long> {
    Customer findByEmailAddressAndPhoneNumber(String emailAddress,String phoneNumber);
}
