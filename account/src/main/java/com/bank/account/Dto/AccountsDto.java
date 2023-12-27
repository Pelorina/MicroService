package com.bank.account.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountsDto {
    private Long accountNumber;
    private String accountType;
    private String branchAddress;
}
