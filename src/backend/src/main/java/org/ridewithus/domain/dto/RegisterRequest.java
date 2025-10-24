package org.ridewithus.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    private String fullName;
    private String userName;
    private String address;
    private String email;
    private String password;
    private String paymentInfo; // Can be enhanced later with a separate Payment entity
}

