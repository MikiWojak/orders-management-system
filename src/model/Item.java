package model;

import java.util.UUID;
import java.math.BigDecimal;

public record Item(
        UUID id,
        String name,
        String description,
        String sku,
        BigDecimal unitNetPrice,
        BigDecimal unitGrossPrice,
        String dimensions,
        String weight
) {
    public Item (
            String name,
            String description,
            String sku,
            BigDecimal singleNetPrice,
            BigDecimal singleGrossPrice,
            String dimension,
            String weight
    ) {
        this(
                UUID.randomUUID(),
                name,
                description,
                sku,
                singleNetPrice,
                singleGrossPrice,
                dimension,
                weight
        );
    }

    public String getFileRecord() {
        return
                this.id + ";" +
                this.name  + ";" +
                this.description  + ";" +
                this.sku  + ";" +
                this.unitNetPrice + ";" +
                this.unitGrossPrice + ";" +
                this.dimensions + ";" +
                this.weight;
    }

    @Override
    public String toString() {
        return "<html>" + name + "<br/>" + sku + "</html>";
    }
}
