package com.golden.system.dto;

import lombok.Data;
import java.util.List;

@Data
public class OrderSubmissionRequest {
    private Long tableId;
    private String notes;
    private List<OrderItemRequest> items;

    @Data
    public static class OrderItemRequest {
        private Long productId;
        private Integer quantity;

        public Long getProductId() {
            return productId;
        }

        public Integer getQuantity() {
            return quantity;
        }
    }
}
