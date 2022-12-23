package com.example.payhere.account.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountRequestDto {

    // 지출 내역
    private Integer money;

    // 메모
    private String memo;
}
