package org.cashinvoice.com.validation;

import org.cashinvoice.com.model.Order;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import jakarta.validation.ValidationException;

@Component
public class OrderValidator implements Processor {
    private static final Logger log = LoggerFactory.getLogger(OrderValidator.class);

    @Override
    public void process(Exchange exchange) throws Exception {
        Order order = exchange.getMessage().getBody(Order.class);
        if (order == null) {
            log.error("Order payload is null");
            throw new ValidationException("Order payload is null");
        }
        if (order.getOrderId() == null || order.getOrderId().isBlank()) {
            throwValidation("orderId must not be null");
        }
        if (order.getCustomerId() == null || order.getCustomerId().isBlank()) {
            throwValidation("customerId must not be null");
        }
        if (order.getAmount() == null || order.getAmount() <= 0) {
            throwValidation("amount must be greater than 0");
        }
    }

    private void throwValidation(String msg) {
        throw new ValidationException(msg);
    }
}

