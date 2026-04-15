package com.team3.device.application.service;

import com.team3.device.domain.model.Customer;
import com.team3.device.domain.repository.CustomerRepository;
import com.team3.device.infrastructure.persistence.repository.JpaCustomerRepository;
import com.team3.device.web.dto.CreateCustomerRequest;
import com.team3.device.web.dto.CustomerResponse;
import com.team3.device.web.exception.DuplicateEmailException;
import com.team3.device.web.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerApplicationServiceTest {

    @Mock
    private CustomerRepository repository;

    @Mock
    private JpaCustomerRepository jpaCustomerRepository;

    @InjectMocks
    private CustomerApplicationService service;

    private CreateCustomerRequest request;
    private Customer savedCustomer;

    @BeforeEach
    void setUp() {
        request = new CreateCustomerRequest();
        request.setName("John Doe");
        request.setEmail("john@example.com");

        savedCustomer = Customer.builder()
                .id(1L)
                .name("John Doe")
                .email("john@example.com")
                .build();
    }

    @Test
    void createCustomer_success() {
        when(jpaCustomerRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
        when(repository.save(any(Customer.class))).thenReturn(savedCustomer);

        CustomerResponse response = service.createCustomer(request);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("John Doe");
        assertThat(response.getEmail()).isEqualTo("john@example.com");

        verify(jpaCustomerRepository).findByEmail("john@example.com");
        verify(repository).save(any(Customer.class));
    }

    @Test
    void createCustomer_duplicateEmail_throwsDuplicateEmailException() {
        when(jpaCustomerRepository.findByEmail(request.getEmail()))
                .thenReturn(Optional.of(savedCustomer));

        assertThatThrownBy(() -> service.createCustomer(request))
                .isInstanceOf(DuplicateEmailException.class)
                .hasMessageContaining("john@example.com");

        verify(repository, never()).save(any());
    }

    @Test
    void getCustomerById_success() {
        when(repository.findById(1L)).thenReturn(Optional.of(savedCustomer));

        CustomerResponse response = service.getCustomerById(1L);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("John Doe");
        assertThat(response.getEmail()).isEqualTo("john@example.com");
    }

    @Test
    void getCustomerById_notFound_throwsResourceNotFoundException() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getCustomerById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }
}