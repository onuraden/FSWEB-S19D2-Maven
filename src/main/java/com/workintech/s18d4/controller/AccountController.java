package com.workintech.s18d4.controller;

import com.workintech.s18d4.dto.AccountResponse;
import com.workintech.s18d4.dto.CustomerResponse;
import com.workintech.s18d4.entity.Account;
import com.workintech.s18d4.entity.Customer;
import com.workintech.s18d4.service.AccountService;
import com.workintech.s18d4.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/account")
public class AccountController {
    private AccountService accountService;
    private CustomerService customerService;

    @Autowired
    public AccountController(AccountService accountService, CustomerService customerService) {
        this.accountService = accountService;
        this.customerService = customerService;
    }

    @GetMapping
    public List<Account> findAll() {
        return accountService.findAll();
    }

    @GetMapping("/{id}")
    public Account find(@PathVariable long id) {
        return accountService.find(id);
    }

    @PostMapping("/{customerId}")
    public AccountResponse save(@PathVariable("customerId") long customerId, @RequestBody Account account) {
        Customer savedCustomer = customerService.find(customerId);
        if(savedCustomer != null) {
            savedCustomer.getAccounts().add(account);
            account.setCustomer(savedCustomer);
            accountService.save(account);
        } else {
            throw new RuntimeException("No customer found!");
        }
        return new AccountResponse(account.getId(), account.getAccountName(), account.getMoneyAmount(),
                new CustomerResponse(savedCustomer.getId(), savedCustomer.getEmail(), savedCustomer.getSalary()));

    }

    @PutMapping("/{customerId}")
    public AccountResponse update(@PathVariable("customerId") long customerId, @RequestBody Account account) {
        Customer updatedCustomer = customerService.find(customerId);
        Account toBeUpdatedAccount = null;

        for(Account account1: updatedCustomer.getAccounts()) {
            if(account.getId() == account1.getId()) {
                toBeUpdatedAccount = account1;
            }
        }

        if(toBeUpdatedAccount == null) {
            throw new RuntimeException("Account not found!");
        }

        int indexOfToBeUpdated = updatedCustomer.getAccounts().indexOf(toBeUpdatedAccount);
        updatedCustomer.getAccounts().set(indexOfToBeUpdated, account);
        account.setCustomer(updatedCustomer);
        accountService.save(account);
        return new AccountResponse(account.getId(), account.getAccountName(), account.getMoneyAmount(),
                new CustomerResponse(updatedCustomer.getId(), updatedCustomer.getEmail(), updatedCustomer.getSalary()));
    }

    @DeleteMapping("/{id}")
    public AccountResponse remove(@PathVariable long id) {
        Account deletedAccount = accountService.find(id);
        if(deletedAccount != null) {
            accountService.delete(id);
            return new AccountResponse(deletedAccount.getId(), deletedAccount.getAccountName(), deletedAccount.getMoneyAmount(),
                    new CustomerResponse(deletedAccount.getCustomer().getId(), deletedAccount.getCustomer().getEmail(), deletedAccount.getCustomer().getSalary()));
        }
        throw new RuntimeException("Account not found!");
    }

}
