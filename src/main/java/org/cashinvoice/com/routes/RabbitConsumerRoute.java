package org.cashinvoice.com.routes;

import org.cashinvoice.com.model.Order;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class RabbitConsumerRoute extends RouteBuilder {

    private static final Logger log = LoggerFactory.getLogger(RabbitConsumerRoute.class);

    @Override
    public void configure() {

        // Do NOT swallow exception → message will NOT be ACKed
        onException(Exception.class)
                .log("FAILED processing RabbitMQ message | Reason=${exception.message}")
                .handled(false);

        //from("spring-rabbitmq:ORDER.CREATED.QUEUE")
        from("spring-rabbitmq:ORDER.CREATED.QUEUE"
                + "?queues=ORDER.CREATED.QUEUE"
                + "&autoDeclare=true")
                .routeId("rabbit-consumer-route")

                // JSON → Order
                .unmarshal().json(JsonLibrary.Jackson, Order.class)

                .process(exchange -> {
                    Order order = exchange.getMessage().getBody(Order.class);

                    if (order == null) {
                        throw new IllegalStateException("Order deserialized as null");
                    }

                    // ============================
                    // REQUIRED LOG (Point 6)
                    // ============================
                    log.info(
                            "Order processed | OrderId={} | CustomerId={} | Amount={}",
                            order.getOrderId(),
                            order.getCustomerId(),
                            order.getAmount()
                    );
                });
    }
}