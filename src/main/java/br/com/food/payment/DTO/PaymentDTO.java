package br.com.food.payment.DTO;

import br.com.food.payment.model.Status;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class PaymentDTO {
    private Long id;
    private BigDecimal amount;
    private String name;
    private String number;
    private String expiration;
    private String code;
    private Status status;
    private Long orderId;
    private Long  payment_method_id;
}
