package model;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public record Order(
        UUID id,
        Date orderedAt,
        List<OrderItem> orderItems,
        BigDecimal netTotal,
        BigDecimal grossTotal,
        Client client,
        Address deliveryAddress
) {
    public Order (
            Date orderedAt,
            List<OrderItem> orderItems,
            BigDecimal netTotal,
            BigDecimal grossTotal,
            Client client,
            Address deliveryAddress
    ) {
        this(
                UUID.randomUUID(),
                orderedAt,
                orderItems,
                netTotal,
                grossTotal,
                client,
                deliveryAddress
        );
    }

    public String getFileRecord() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
        String orderedAtIso = dateFormat.format((this.orderedAt));

        return
                this.id + ";" +
                orderedAtIso + ";" +
                this.netTotal + ";" +
                this.grossTotal + ";" +
                this.client.id()  + ";" +
                this.deliveryAddress.getFileRecord();
    }

    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
    }
}
