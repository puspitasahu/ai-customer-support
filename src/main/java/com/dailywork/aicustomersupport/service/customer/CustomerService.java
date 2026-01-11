package com.dailywork.aicustomersupport.service.customer;

import com.dailywork.aicustomersupport.model.Customer;
import com.dailywork.aicustomersupport.repository.CustomerRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerService implements ICustomerService{

    private final CustomerRepository customerRepository;

    @Override
    public Customer createCustomer(final Customer customer){
        return customerRepository.save(customer);
    }

    @Override
    public Customer getCustomerById(final Long id){
        return customerRepository.findById(id)
                .orElseThrow(()->new EntityNotFoundException("Customer Not Found"));
    }

    @Override
    public Customer getCustomerByEmail(final String emails){
        return customerRepository.findByEmailAddress(emails)
                .orElseThrow(()->new EntityNotFoundException("Customer Not Found"));
    }

    @Override
    public List<Customer> getAllCustomers(){
        return List.of();
    }

    @Override
    public Customer updateCustomer(final Long id, final Customer updateCustomer){
        return null;
    }

    @Override
    public void deleteCustomer(){

    }
}
