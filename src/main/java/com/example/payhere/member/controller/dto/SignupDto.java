package com.example.payhere.member.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignupDto {

    @NotBlank
    @Pattern(regexp ="^[a-zA-Z0-9]+@[a-zA-Z]+.[a-z]+${4,12}$")
    private String email;

    @NotBlank
    private String pw;

    @NotBlank
    private String pwConfirm;
}
