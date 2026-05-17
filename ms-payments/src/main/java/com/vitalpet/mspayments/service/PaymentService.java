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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PaymentService {

    @Autowired private PaymentRepository paymentRepository;
    @Autowired private PaymentStatusRepository paymentStatusRepository;
    @Autowired private PaymentMethodRepository paymentMethodRepository;
    @Autowired private NotificationClient notificationClient;

    public PaymentResponseDTO create(PaymentRequestDTO paymentRequestDTO){
        PaymentStatus pending = paymentStatusRepository.findByName("PENDING")
                .orElseThrow(() -> new ResourceNotFoundException("Estado PENDING no Encontrado"));

        Payment payment = new Payment();
        payment.setAmount(paymentRequestDTO.getAmount());
        payment.setUserId(paymentRequestDTO.getUserId());
        payment.setAppointmentId(paymentRequestDTO.getAppointmentId());
        payment.setStatus(pending);

        return toDTO(paymentRepository.save(payment));
    }

    //Lógica de pago
    public PaymentResponseDTO pay(Long id, String methodName){
        Payment payment  = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pago no encontrado"));

        PaymentStatus paidStatus = paymentStatusRepository.findByName("PAID")
                .orElseThrow(() -> new ResourceNotFoundException("Estado PAID no existe"));

        PaymentMethod method = paymentMethodRepository.findByName(methodName)
                .orElseThrow(() -> new ResourceNotFoundException("Método de pago " + methodName + " no existe"));

        payment.setStatus(paidStatus);
        payment.setMethod(method);
        payment.setPaidAt(LocalDateTime.now());

        Payment savedPayment = paymentRepository.save(payment);

        //Aquí debería ir la lógica de la notificación
        NotificationRequestDTO notificationDTO = new NotificationRequestDTO();
        notificationDTO.setUserId(savedPayment.getUserId());
        notificationDTO.setType("PAYMENT_CONFIRMED");
        notificationDTO.setMessage("Su pago de: $"+savedPayment.getAmount() + " en VitalPetConnect ha sido procesado de manera exitosa.");

        // Aca ahcemos trycatch por si notificaciones está off
        try {
            notificationClient.sendNotification(notificationDTO);
        } catch (Exception e) {
            // Aca no lanzamos excepción porque el pago ya está hecho
            System.err.println("Advertencia: El pago fue guardado como PAID, pero ms-notifications no pudo enviar el correo de confirmación.");
        }

        return toDTO(savedPayment);

    }

    public PaymentResponseDTO refund(Long id){
        Payment payment  = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pago no encontrado"));

        PaymentStatus refunded = paymentStatusRepository.findByName("REFUNDED")
                .orElseThrow(() -> new ResourceNotFoundException("Estado REFUNDED no existe"));

        payment.setStatus(refunded);
        return toDTO(paymentRepository.save(payment));
    }

    //Traer todos
    public List<PaymentResponseDTO> getAll(){
        return paymentRepository.findAll().stream().map(this::toDTO).toList();
    }

    //Traer payments pendientes de un usuario
    public List<PaymentResponseDTO> getPendingByUser(Long id){
        return paymentRepository.findByUserIdAndStatusName(id,"PENDING").stream().map(this::toDTO).toList();
    }


    // Payment TO DTO
    private PaymentResponseDTO toDTO(Payment payment){
        PaymentResponseDTO dto = new PaymentResponseDTO();
        dto.setId(payment.getId());
        dto.setAmount(payment.getAmount());
        dto.setPaidAt(payment.getPaidAt());
        dto.setCreatedAt(payment.getCreatedAt());

        if(payment.getStatus() != null){
            dto.setStatusName(payment.getStatus().getName());
        }

        if(payment.getMethod() != null){
            dto.setMethodName(payment.getMethod().getName());
        } else {
            dto.setMethodName(null);
        }

        dto.setUserId(payment.getUserId());
        dto.setAppointmentId(payment.getAppointmentId());
        return dto;
    }

}
