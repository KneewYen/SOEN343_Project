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
    private int flexdollarbalance;

//    public UserDTO(Long id, String fullName, String userName, String email, String role, String address, PricingPlan pricingPlan, int flexdollarbalance) {
//        this.id = id;
//        this.fullName = fullName;
//        this.userName = userName;
//        this.email = email;
//        this.role = role;
//        this.address = address;
//        this.pricingPlan = pricingPlan;
//        this.flexdollarbalance = flexdollarbalance;
//    }
    private PricingPlan pricingPlan;
    private Tier loyaltyTier;
    private Tier prevLoyaltyTier;

    public static UserDTO fromEntity(User u) {
        return UserDTO.builder()
                .id(u.getId())
                .userName(u.getUserName())
                .build();
    }
}

