package br.com.food.payment.service;

import br.com.food.payment.DTO.PaymentDTO;
import br.com.food.payment.http.OrderClient;
import br.com.food.payment.model.Payment;
import br.com.food.payment.model.Status;
import br.com.food.payment.repository.PaymentRepository;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PaymentService {
    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private OrderClient orderClient;

    public Page<PaymentDTO> getAllPayments(Pageable pagination) {
        return paymentRepository
                .findAll(pagination)
                .map(p -> modelMapper.map(p, PaymentDTO.class));
    }

    public PaymentDTO getPaymentById(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);

        return modelMapper.map(payment, PaymentDTO.class);
    }

    public PaymentDTO createPayment(PaymentDTO paymentDTO) {
        Payment payment = modelMapper.map(paymentDTO, Payment.class);
        payment.setStatus(Status.CRIADO);
        paymentRepository.save(payment);

        return modelMapper.map(payment, PaymentDTO.class);
    }

    public PaymentDTO updatePayment(Long id, PaymentDTO paymentDTO) {
        Payment payment = modelMapper.map(paymentDTO, Payment.class);
        payment.setId(id);
        payment = paymentRepository.save(payment);

        return modelMapper.map(payment, PaymentDTO.class);
    }

    public void deletePayment(Long id) {
        paymentRepository.deleteById(id);
    }

    public void confirmPayment(Long id) {
        Optional<Payment> payment = paymentRepository.findById(id);
        if(!payment.isPresent()) {
            throw new EntityNotFoundException();
        }

        payment.get().setStatus(Status.CONFIRMADO);
        paymentRepository.save(payment.get());
        orderClient.updatePayment(payment.get().getOrderId());
    }

    public void updateStatus(Long id) {
        Optional<Payment> payment = paymentRepository.findById(id);
        if(!payment.isPresent()) {
            throw new EntityNotFoundException();
        }

        payment.get().setStatus(Status.CONFIRMADO_SEM_INTEGRACAO);
        paymentRepository.save(payment.get());
    }
}
