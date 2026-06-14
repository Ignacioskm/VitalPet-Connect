package com.vitalpet.mspayments.service;

import com.vitalpet.mspayments.client.NotificationClient;
import com.vitalpet.mspayments.dto.NotificationRequestDTO;
import com.vitalpet.mspayments.dto.PaymentRequestDTO;
import com.vitalpet.mspayments.dto.PaymentResponseDTO;
import com.vitalpet.mspayments.exception.ResourceNotFoundException;
import com.vitalpet.mspayments.model.Payment;
import com.vitalpet.mspayments.model.PaymentMethod;
import com.vitalpet.mspayments.model.PaymentStatus;
import com.vitalpet.mspayments.repository.PaymentMethodRepository;
import com.vitalpet.mspayments.repository.PaymentRepository;
import com.vitalpet.mspayments.repository.PaymentStatusRepository;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock private PaymentRepository paymentRepository;
    @Mock private PaymentStatusRepository paymentStatusRepository;
    @Mock private PaymentMethodRepository paymentMethodRepository;
    @Mock private NotificationClient notificationClient;

    @InjectMocks private PaymentService paymentService;

    private Faker faker;
    private Payment mockPayment;
    private PaymentStatus statusPending;
    private PaymentStatus statusPaid;
    private PaymentStatus statusRefunded;
    private PaymentMethod methodCard;
    private PaymentRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        faker = new Faker();

        statusPending = new PaymentStatus(1L, "PENDING");
        statusPaid = new PaymentStatus(2L, "PAID");
        statusRefunded = new PaymentStatus(3L, "REFUNDED");

        methodCard = new PaymentMethod(1L, "CARD");

        mockPayment = new Payment();
        mockPayment.setId(1L);
        mockPayment.setAmount(25000.0);
        mockPayment.setStatus(statusPending); // Estado inicial
        mockPayment.setMethod(null); // Sin método aún
        mockPayment.setUserId(10L);
        mockPayment.setAppointmentId(20L);

        requestDTO = new PaymentRequestDTO(
                mockPayment.getAmount(),
                mockPayment.getUserId(),
                mockPayment.getAppointmentId()
        );
    }


    @Test
    void create_ShouldReturnPaymentDTO_WhenStatusPendingExists() {
        when(paymentStatusRepository.findByName("PENDING")).thenReturn(Optional.of(statusPending));

        // Usamos thenAnswer para devolver la entidad que el servicio construye internamente PD: GRACIAS IA
        when(paymentRepository.save(any(Payment.class))).thenAnswer(i -> i.getArguments()[0]);

        PaymentResponseDTO result = paymentService.create(requestDTO);

        assertNotNull(result);
        assertEquals("PENDING", result.getStatusName());
        assertNull(result.getMethodName());
        assertEquals(25000.0, result.getAmount());

        verify(paymentRepository, times(1)).save(any(Payment.class));
    }

    @Test
    void create_ShouldThrowResourceNotFoundException_WhenStatusPendingDoesNotExist() {
        when(paymentStatusRepository.findByName("PENDING")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> paymentService.create(requestDTO));
        verify(paymentRepository, never()).save(any());
    }


    @Test
    void pay_ShouldChangeStatusToPaid_AndSendNotification() {
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(mockPayment));
        when(paymentStatusRepository.findByName("PAID")).thenReturn(Optional.of(statusPaid));
        when(paymentMethodRepository.findByName("CARD")).thenReturn(Optional.of(methodCard));
        when(paymentRepository.save(any(Payment.class))).thenReturn(mockPayment);

        PaymentResponseDTO result = paymentService.pay(1L, "CARD");

        assertNotNull(result);
        assertEquals("PAID", result.getStatusName());
        assertEquals("CARD", result.getMethodName()); // Ahora sí pasa por el IF del método
        assertNotNull(result.getPaidAt());

        verify(notificationClient, times(1)).sendNotification(any(NotificationRequestDTO.class));
    }

    @Test
    void pay_ShouldReturnDTO_EvenIfNotificationClientFails() {
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(mockPayment));
        when(paymentStatusRepository.findByName("PAID")).thenReturn(Optional.of(statusPaid));
        when(paymentMethodRepository.findByName("CARD")).thenReturn(Optional.of(methodCard));
        when(paymentRepository.save(any(Payment.class))).thenReturn(mockPayment);

        doThrow(new RuntimeException("ms-notifications está apagado")).when(notificationClient).sendNotification(any());

        PaymentResponseDTO result = paymentService.pay(1L, "CARD");

        assertNotNull(result);
        assertEquals("PAID", result.getStatusName());
    }

    @Test
    void pay_ShouldThrowResourceNotFoundException_WhenMethodNotFound() {
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(mockPayment));
        when(paymentStatusRepository.findByName("PAID")).thenReturn(Optional.of(statusPaid));
        when(paymentMethodRepository.findByName("BITCOIN")).thenReturn(Optional.empty()); // Método inventado

        assertThrows(ResourceNotFoundException.class, () -> paymentService.pay(1L, "BITCOIN"));
        verify(paymentRepository, never()).save(any());
    }

    @Test
    void refund_ShouldChangeStatusToRefunded() {
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(mockPayment));
        when(paymentStatusRepository.findByName("REFUNDED")).thenReturn(Optional.of(statusRefunded));
        when(paymentRepository.save(any(Payment.class))).thenReturn(mockPayment);

        PaymentResponseDTO result = paymentService.refund(1L);

        assertNotNull(result);
        assertEquals("REFUNDED", result.getStatusName());
    }

    @Test
    void getAll_ShouldReturnListOfPayments() {
        when(paymentRepository.findAll()).thenReturn(List.of(mockPayment));
        List<PaymentResponseDTO> result = paymentService.getAll();
        assertEquals(1, result.size());
    }

    @Test
    void getPendingByUser_ShouldReturnListOfPendingPayments() {
        Long userId = 10L;
        when(paymentRepository.findByUserIdAndStatusName(userId, "PENDING")).thenReturn(List.of(mockPayment));
        List<PaymentResponseDTO> result = paymentService.getPendingByUser(userId);

        assertEquals(1, result.size());
        verify(paymentRepository, times(1)).findByUserIdAndStatusName(userId, "PENDING");
    }
}