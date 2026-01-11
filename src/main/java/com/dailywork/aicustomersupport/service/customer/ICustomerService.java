package com.dailywork.aicustomersupport.service.customer;

import com.dailywork.aicustomersupport.model.Customer;

import java.util.List;

public interface ICustomerService{
    Customer createCustomer(Customer customer);
    Customer getCustomerById(Long id);
    Customer getCustomerByEmail(String emails,String phoneNumber);
    List<Customer> getCustomers();
    Customer updateCustomer(Long id,Customer updateCustomer);
    void deleteCustomer();

}
