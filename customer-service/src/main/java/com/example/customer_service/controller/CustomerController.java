package com.example.customer_service.controller;

import com.example.customer_service.model.Customer;
import com.example.customer_service.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @GetMapping
    public List<Customer> getAllCustomers() {
        return customerService.getAllCustomers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Customer> getCustomerById(@PathVariable Long id) {
        return customerService.findByCustomerId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Customer createCustomer(@RequestBody Customer customer) {
        return customerService.createCustomer(customer);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Customer> updateCustomer(@PathVariable Long id, @RequestBody Customer customer) {
        Customer updatedCustomer = customerService.updateCustomer(id, customer);
        return (updatedCustomer != null) ? ResponseEntity.ok(updatedCustomer) : ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}/suspend")
    public ResponseEntity<Customer> suspendCustomer(@PathVariable Long id) {
        Customer suspendedCustomer = customerService.suspendCustomer(id);
        return (suspendedCustomer != null) ? ResponseEntity.ok(suspendedCustomer) : ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}/activate")
    public ResponseEntity<Customer> activateCustomer(@PathVariable Long id) {
        Customer activatedCustomer = customerService.activateCustomer(id);
        return (activatedCustomer != null) ? ResponseEntity.ok(activatedCustomer) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/stats/count")
    public ResponseEntity<Long> getCustomerCount() {
        return ResponseEntity.ok(customerService.getCustomerCount());
    }

    @GetMapping("/recent")
    public ResponseEntity<List<Customer>> getRecentCustomers() {
        return ResponseEntity.ok(customerService.getRecentCustomers());
    }
}
