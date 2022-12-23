package com.example.payhere.account.controller.dto;


import com.example.payhere.shared.domain.Timestamped;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountResponseDto extends Timestamped {

    // 아이디
    private Long id;

    // 지출 내역
    private Integer money;

    // 메모
    private String memo;

    // 작성 시간
    private LocalDateTime createdAt;

    // 수정 시간
    private LocalDateTime modifiedAt;
}
