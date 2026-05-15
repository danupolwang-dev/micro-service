package com.example.customer_service.service;

import com.example.customer_service.model.Customer;
import com.example.customer_service.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    public Optional<Customer> findByCustomerId(Long id) {
        return customerRepository.findById(id);
    }

    public void deleteCustomer(Long id) {
        customerRepository.deleteById(id);
    }

    public Long getCustomerCount() {
        return customerRepository.count();
    }

    public List<Customer> getRecentCustomers() {
        return customerRepository.findAll().stream()
                .sorted((c1, c2) -> c2.getCreatedDt().compareTo(c1.getCreatedDt()))
                .limit(5)
                .collect(Collectors.toList());
    }

    public Customer createCustomer(Customer customer) {
        return customerRepository.save(customer);
    }

    public Customer updateCustomer(Long id, Customer customerDetails) {
        return customerRepository.findById(id).map(customer -> {
            customer.setName(customerDetails.getName());
            customer.setEmail(customerDetails.getEmail());
            customer.setMobileNo(customerDetails.getMobileNo());
            // Update other fields as needed, but usually not status or audit fields
            // directly here
            return customerRepository.save(customer);
        }).orElse(null);
    }

    public Customer suspendCustomer(Long id) {
        return customerRepository.findById(id).map(customer -> {
            customer.setStatus("SUSPENDED");
            return customerRepository.save(customer);
        }).orElse(null);
    }

    public Customer activateCustomer(Long id) {
        return customerRepository.findById(id).map(customer -> {
            customer.setStatus("ACTIVE");
            return customerRepository.save(customer);
        }).orElse(null);
    }
}
