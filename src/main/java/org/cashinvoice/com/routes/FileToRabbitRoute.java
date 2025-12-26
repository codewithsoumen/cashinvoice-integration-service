package org.cashinvoice.com.routes;

import org.cashinvoice.com.model.Order;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.stereotype.Component;

@Component
public class FileToRabbitRoute extends RouteBuilder {

    @Override
    public void configure() {

        // ============================
        // Global exception handling
        // ============================
        onException(Exception.class)
                .handled(true)
                .log("FAILED processing file ${header.CamelFileName} | Reason=${exception.message}")
                // convert POJO → JSON before writing error file
                .marshal().json(JsonLibrary.Jackson)
                .to("file:{{integration.error.dir}}");

        from("file:{{integration.input.dir}}?include=order-.*\\.json&readLock=changed")
                .routeId("file-to-rabbit-route")

                // File JSON → Order
                .unmarshal().json(JsonLibrary.Jackson, Order.class)

                // ============================
                // REQUIRED LOGS (Point 6)
                // ============================
                .log("File processed | FileName=${header.CamelFileName} | OrderId=${body.orderId}")
                .log("Success → send to ORDER.CREATED.QUEUE | OrderId=${body.orderId}")

                // ============================
                // Order → JSON (MANDATORY)
                // ============================
                .marshal().json(JsonLibrary.Jackson)

                // Send JSON to RabbitMQ
                .to("spring-rabbitmq:ORDER.CREATED.QUEUE");
    }
}