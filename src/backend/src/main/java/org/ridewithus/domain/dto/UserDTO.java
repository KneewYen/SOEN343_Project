package org.ridewithus.domain.dto;

import org.ridewithus.domain.entity.PricingPlan;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ridewithus.domain.entity.User;
import org.ridewithus.domain.loyaltyProgram.ChainOfR.Tier;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long id;
    private String fullName;
    private String userName;
    private String email;
    private String role;
    private String address;
    private PricingPlan pricingPlan;
    private Tier loyaltyTier;

    public static UserDTO fromEntity(User u) {
        return UserDTO.builder()
                .id(u.getId())
                .userName(u.getUserName())
                .build();
    }
}

