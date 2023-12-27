package Loans.Loans.Service;

import Loans.Loans.Dto.LoansDto;

public interface ILoansService {


    void createLoan(String mobileNumber);
    LoansDto fetchLoan(String mobileNumber);


    boolean updateLoan(LoansDto loansDto);

    boolean deleteLoan(String mobileNumber);

}
