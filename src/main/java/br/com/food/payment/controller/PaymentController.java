package br.com.food.payment.controller;

import br.com.food.payment.DTO.PaymentDTO;
import br.com.food.payment.service.PaymentService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.apache.coyote.Response;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;


import java.net.URI;

@RestController
@RequestMapping("/pagamentos")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @GetMapping
    public Page<PaymentDTO> list(@PageableDefault(size = 10)Pageable pagination) {
        return paymentService.getAllPayments(pagination);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentDTO> getOne(@PathVariable @NotNull Long id) {
        PaymentDTO paymentDTO = paymentService.getPaymentById(id);

        return ResponseEntity.ok(paymentDTO);
    }

    @PostMapping
    public ResponseEntity<PaymentDTO> create(@RequestBody @Valid PaymentDTO paymentDTO, UriComponentsBuilder uriComponentsBuilder) {
        PaymentDTO payment = paymentService.createPayment(paymentDTO);
        URI address = uriComponentsBuilder.path("/pagamentos/{id}").buildAndExpand(payment.getId()).toUri();

        rabbitTemplate.convertAndSend("pagamentos.ex","", payment);
        return ResponseEntity.created(address).body(payment);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PaymentDTO> update(@PathVariable @NotNull Long id, @RequestBody @Valid PaymentDTO paymentDTO) {
        PaymentDTO updated = paymentService.updatePayment(id, paymentDTO);

        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<PaymentDTO> delete(@PathVariable @NotNull Long id) {
        paymentService.deletePayment(id);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/confirmar")
    @CircuitBreaker(name = "updateOrder", fallbackMethod ="authorizedPaymentWithHoldingIntegration")
    public void confirmPayment(@PathVariable @NotNull Long id) {
        paymentService.confirmPayment(id);
    }

    public void authorizedPaymentWithHoldingIntegration(Long id, Exception e){
        paymentService.updateStatus(id);
    }
}
