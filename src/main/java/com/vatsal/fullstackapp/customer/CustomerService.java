package com.vatsal.fullstackapp.customer;

import com.vatsal.fullstackapp.exception.DuplicateResourceException;
import com.vatsal.fullstackapp.exception.RequestValidationException;
import com.vatsal.fullstackapp.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {

    private final CustomerDao customerDao;

    public CustomerService(@Qualifier("jdbc") CustomerDao customerDao) {
        this.customerDao = customerDao;
    }

    public List<Customer> getAllCustomers() {
        return customerDao.getAllCustomers();
    }

    public Customer getCustomer(Integer id) {
        return customerDao.getCustomerById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Customer with id %d not found".formatted(id)
                ));
    }

    public void addCustomer(CustomerRegistrationRequest customerRegistrationRequest) {
        if (customerRegistrationRequest.email() == null || customerRegistrationRequest.email().isBlank()) {
            throw new IllegalArgumentException("Email cannot be null or blank");
        }
        if (customerDao.existsPersonWithEmail(customerRegistrationRequest.email())) {
            throw new DuplicateResourceException(
                    "Email is already taken"
            );
        }

        Customer customer = new Customer(
                customerRegistrationRequest.name(),
                customerRegistrationRequest.email(),
                customerRegistrationRequest.age()
        );
        customerDao.insertCustomer(customer);
    }

    public void deleteCustomer(Integer id) {
        if (!customerDao.existsPersonWithId(id)) {
            throw new ResourceNotFoundException(
                    String.format("Customer with id %d not found", id)
            );
        }
        customerDao.deleteCustomerById(id);
    }

    public void updateCustomer(Integer id, CustomerUpdateRequest customerRegistrationRequest) {
        if (customerDao.getCustomerById(id).isEmpty()) {
            throw new ResourceNotFoundException(
                    String.format("Customer with id %d not found", id)
            );
        }
        Customer customer = getCustomer(id);

        boolean changes = false;
        if (customerRegistrationRequest.email() != null && !customerRegistrationRequest.email().isBlank()) {
            if (!customer.getEmail().equals(customerRegistrationRequest.email())) {
                if (customerDao.existsPersonWithEmail(customerRegistrationRequest.email())) {
                    throw new DuplicateResourceException("Email is already taken");
                }
                customer.setEmail(customerRegistrationRequest.email());
                changes = true;
            }
        }
        if (customerRegistrationRequest.name() != null && !customerRegistrationRequest.name().isBlank()) {
            if (!customer.getName().equals(customerRegistrationRequest.name())) {
                customer.setName(customerRegistrationRequest.name());
                changes = true;
            }
        }
        if (customerRegistrationRequest.age() != null && customerRegistrationRequest.age() > 0) {
            if (!customer.getAge().equals(customerRegistrationRequest.age())) {
                customer.setAge(customerRegistrationRequest.age());
                changes = true;
            }
        }

        if (!changes) {
            throw new RequestValidationException("No changes to update");
        }

        customerDao.updateCustomer(customer);
    }
}
