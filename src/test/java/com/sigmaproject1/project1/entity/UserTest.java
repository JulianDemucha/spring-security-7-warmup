package com.sigmaproject1.project1.entity;

import com.sigmaproject1.project1.enums.Role;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UserTest {

    @Test
    void builder_clientWithCustomer_builds() {
        Customer customer = new Customer();
        User user = assertDoesNotThrow( () ->
                User.builder()
                        .customer(customer)
                        .role(Role.CLIENT)
                        .build()
        );
        assertEquals(Role.CLIENT, user.getRole());
        assertEquals(customer, user.getCustomer());
    }

    @Test
    void builder_clientWithoutCustomer_throws() {
        assertThrows(IllegalArgumentException.class, () ->
                User.builder()
                        .customer(null)
                        .role(Role.CLIENT)
                        .build()
        );
    }

    @Test
    void builder_adminWithoutCustomer_builds() {
        User user = assertDoesNotThrow( () ->
                User.builder()
                        .customer(null)
                        .role(Role.ADMIN)
                        .build()
        );

        assertEquals(Role.ADMIN, user.getRole());
        assertNull(user.getCustomer());
    }

    @Test
    void builder_adminWithCustomer_throws() {
        assertThrows(IllegalArgumentException.class, () ->
                User.builder()
                        .customer(new Customer())
                        .role(Role.ADMIN)
                        .build()
        );
    }

    @Test
    void builder_nullRole_throws() {
        assertThrows(IllegalArgumentException.class, () ->
                User.builder()
                        .role(null)
                        .build()
        );
    }
}
