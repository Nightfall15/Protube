package com.tecnocampus.LS2.protube_back.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDTO {
    private Long id;
    private String username;
    private String email;
    private String name;
    private String surname;
    private String number;
}
