package org.ridewithus.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillingDTO {
    private Long billingId;
    private Long tripId;
    private List<ChargeDTO> charges;
    private int flexDollars;  // Deprecated - use flexDollarDiscount instead
    private Double totalAmount;
    private int flexDollarDiscount;  // Amount of flex dollars applied as discount
    private int finalAmount;  // Amount after flex dollar discount (in cents or as integer)
}

