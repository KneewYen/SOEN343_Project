package org.ridewithus.domain.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long id;

    private String fullName;

    private String userName;

    private String address;

    private String email;

    private String role = "rider"; //user are automatically user unless specified otherwise

    private String password ;
}
