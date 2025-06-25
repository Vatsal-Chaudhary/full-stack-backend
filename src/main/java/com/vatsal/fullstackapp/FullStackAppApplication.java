package com.vatsal.fullstackapp;

import com.github.javafaker.Faker;
import com.github.javafaker.Name;
import com.vatsal.fullstackapp.customer.Customer;
import com.vatsal.fullstackapp.customer.CustomerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;
import java.util.Random;

@SpringBootApplication
public class FullStackAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(FullStackAppApplication.class, args);
    }

    @Bean
    CommandLineRunner runner(CustomerRepository customerRepository) {
        return args -> {
            Faker faker = new Faker();
            Random random = new Random();
            Name name = faker.name();
            String firstName = name.firstName();
            String lastName = name.lastName();
            Customer customer = new Customer(firstName + " " + lastName,
                    firstName.toLowerCase() + "." + lastName.toLowerCase() + "@gmail.com",
                    random.nextInt(16, 99));
            customerRepository.save(customer);
        };
    }

}
