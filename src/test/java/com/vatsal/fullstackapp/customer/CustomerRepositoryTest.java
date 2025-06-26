package com.vatsal.fullstackapp.customer;

import com.vatsal.fullstackapp.AbstractTestContainers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.ApplicationContext;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CustomerRepositoryTest extends AbstractTestContainers {

    @Autowired
    private CustomerRepository underTest;

    @Autowired
    private ApplicationContext applicationContext;

    @BeforeEach
    void setUp() {
        System.out.println(applicationContext.getBeanDefinitionCount());
    }

    @Test
    void existsByEmail() {
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        String name = FAKER.name().fullName();
        Customer customer = new Customer(
                name,
                email,
                20
        );

        underTest.save(customer);

        boolean actual = underTest.existsByEmail(email);

        assertThat(actual).isTrue();
    }

    @Test
    void existsByEmailFailsWhenEmailNotPresent() {
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();

        boolean actual = underTest.existsByEmail(email);

        assertThat(actual).isFalse();
    }

    @Test
    void existsById() {
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        Customer customer = new Customer(
                FAKER.name().fullName(),
                email,
                20
        );

        underTest.save(customer);

        int id = underTest.findAll()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Customer not found with email: " + email));

        var actual = underTest.existsById(id);

        assertThat(actual).isTrue();
    }

    @Test
    void existsByIdWhenIdNotPresent() {

        int id = -1;

        var actual = underTest.existsById(id);

        assertThat(actual).isFalse();
    }
}