package br.com.food.payment.repository;

import br.com.food.payment.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long>{
}
