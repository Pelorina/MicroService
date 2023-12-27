package com.bank.account.Service.Impl;

import com.bank.account.Constants.AccountConstants;
import com.bank.account.Dto.AccountsDto;
import com.bank.account.Dto.CustomerDto;
import com.bank.account.Entity.Accounts;
import com.bank.account.Entity.Customer;
import com.bank.account.Exception.CustomerAlreadyExistsException;
import com.bank.account.Exception.ResourceNotFoundException;
import com.bank.account.Mapper.AccountsMapper;
import com.bank.account.Mapper.CustomerMapper;
import com.bank.account.Repository.AccountsRepository;
import com.bank.account.Repository.CustomerRepository;
import com.bank.account.Service.IAccountsService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;

@Service
@AllArgsConstructor
public class AccountsServiceImpl implements IAccountsService {
    private AccountsRepository accountsRepository;
    private CustomerRepository customerRepository;
    @Override
    public void createAccount(CustomerDto customerDto) {
        Customer customer = CustomerMapper.mapToCustomer(customerDto, new Customer());
        Optional<Customer> optionalCustomer = customerRepository.findByMobileNumber(customerDto.getMobileNumber());
        if(optionalCustomer.isPresent()) {
            throw new CustomerAlreadyExistsException("Customer already registered with given mobileNumber "
                    +customerDto.getMobileNumber());
        }

        Customer savedCustomer = customerRepository.save(customer);
        accountsRepository.save(createNewAccount(savedCustomer));
    }
    private Accounts createNewAccount(Customer customer) {
        Accounts newAccount = new Accounts();
        newAccount.setCustomerId(customer.getCustomerId());
        long randomAccNumber = 1000000000L + new Random().nextInt(900000000);
        newAccount.setAccountNumber(randomAccNumber);
        newAccount.setAccountType(AccountConstants.SAVINGS);
        newAccount.setBranchAddress(AccountConstants.ADDRESS);
        return newAccount;
    }
    @Override
    public CustomerDto fetchAccount(String mobileNumber) {
        Customer customer = customerRepository.findByMobileNumber(mobileNumber).orElseThrow(
                () -> new ResourceNotFoundException("Customer", "mobileNumber", mobileNumber)
        );
//   # if you want to get information from two table to appear use this
        Accounts accounts = accountsRepository.findByCustomerId(customer.getCustomerId()).orElseThrow(
                () -> new ResourceNotFoundException("Account", "customerId", customer.getCustomerId().toString())
        );
        CustomerDto customerDto = CustomerMapper.mapToCustomerDto(customer, new CustomerDto());
        customerDto.setAccountsDto(AccountsMapper.mapToAccountsDto(accounts, new AccountsDto()));
        return customerDto;
    }
//    public CustomerDto fetchAccount(String mobileNumber) {
//        // Find the customer by mobile number
//        Customer customer = customerRepository.findByMobileNumber(mobileNumber)
//                .orElseThrow(() -> new ResourceNotFoundException("Customer", "mobileNumber", mobileNumber));
//
//        // Find the account by customer ID
//        Accounts accounts = accountsRepository.findByCustomerId(customer.getCustomerId())
//                .orElseThrow(() -> new ResourceNotFoundException("Account", "customerId", customer.getCustomerId().toString()));
//
//        // Create a new CustomerDto and set properties from Customer entity
//        CustomerDto customerDto = new CustomerDto();
//        customerDto.setId(customer.getId());
//        customerDto.setName(customer.getName());
//        // ... set other customer properties ...
//
//        // Create a new AccountsDto and set properties from Accounts entity
//        AccountsDto accountsDto = new AccountsDto();
//        accountsDto.setAccountId(accounts.getAccountId());
//        accountsDto.setBalance(accounts.getBalance());
//        // ... set other accounts properties ...
//
//        // Set AccountsDto to CustomerDto
//        customerDto.setAccountsDto(accountsDto);
//
//        return customerDto;
//    }


    /**
     * @return boolean indicating if the update of Account details is successful or not
     */
    @Override
    public boolean updateAccount(CustomerDto customerDto) {
        boolean isUpdated = false;
        AccountsDto accountsDto = customerDto.getAccountsDto();
        if(accountsDto !=null ){
            Accounts accounts = accountsRepository.findById(accountsDto.getAccountNumber()).orElseThrow(
                    () -> new ResourceNotFoundException("Account", "AccountNumber", accountsDto.getAccountNumber().toString())
            );
            AccountsMapper.mapToAccounts(accountsDto, accounts);
            accounts = accountsRepository.save(accounts);

            Long customerId = accounts.getCustomerId();
            Customer customer = customerRepository.findById(customerId).orElseThrow(
                    () -> new ResourceNotFoundException("Customer", "CustomerID", customerId.toString())
            );
            CustomerMapper.mapToCustomer(customerDto,customer);
            customerRepository.save(customer);
            isUpdated = true;
        }
        return  isUpdated;
    }

    /**
     * @return boolean indicating if the delete of Account details is successful or not
     */
    @Override
    public boolean deleteAccount(String mobileNumber) {
        Customer customer = customerRepository.findByMobileNumber(mobileNumber).orElseThrow(
                () -> new ResourceNotFoundException("Customer", "mobileNumber", mobileNumber)
        );
        accountsRepository.deleteByCustomerId(customer.getCustomerId());
        customerRepository.deleteById(customer.getCustomerId());
        return true;
    }



}
