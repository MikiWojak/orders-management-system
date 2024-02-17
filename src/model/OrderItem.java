package model;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public record OrderItem(
        UUID id,
        Item item,
        int count,
        int discount,
        BigDecimal netSum,
        BigDecimal grossSum
) {
    public OrderItem (
            Item item,
            int count,
            int discount,
            BigDecimal netSum,
            BigDecimal grossSum
    ) {
        this(
                UUID.randomUUID(),
                item,
                count,
                discount,
                netSum,
                grossSum
        );
    }

    public String getFileRecord(UUID orderId) {
        return
                this.id + ";" +
                orderId + ";" +
                this.item.id()  + ";" +
                this.count  + ";" +
                this.discount  + ";" +
                this.netSum  + ";" +
                this.grossSum;
    }
}
