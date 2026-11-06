package com.sigmaproject1.project1.service;

import com.sigmaproject1.project1.exception.UserNotFoundException;
import com.sigmaproject1.project1.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void getById_userDoesNotExist_throws() {
        //Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        //Act and Assert
        assertThrows(UserNotFoundException.class, () -> userService.getById(1L));
    }
}
