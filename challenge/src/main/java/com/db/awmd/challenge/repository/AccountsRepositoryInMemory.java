package com.db.awmd.challenge.repository;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.dto.FundTransfer;
import com.db.awmd.challenge.dto.ResponseFundTransfer;
import com.db.awmd.challenge.exception.DuplicateAccountIdException;
import com.db.awmd.challenge.service.EmailNotificationService;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;

@Repository
public class AccountsRepositoryInMemory implements AccountsRepository {

  private final Map<String, Account> accounts = new ConcurrentHashMap<>();

  @Override
  public void createAccount(Account account) throws DuplicateAccountIdException {
    Account previousAccount = accounts.putIfAbsent(account.getAccountId(), account);
    if (previousAccount != null) {
      throw new DuplicateAccountIdException(
        "Account id " + account.getAccountId() + " already exists!");
    }
  }

  @Override
  public Account getAccount(String accountId) {
    return accounts.get(accountId);
  }

  @Override
  public void clearAccounts() {
    accounts.clear();
  }

@Override
public ResponseFundTransfer transaction(FundTransfer fundTransfer) {
	addAmount(fundTransfer.getToAccount(), fundTransfer.getAmount());
    addAmount(fundTransfer.getFromAccount(), fundTransfer.getAmount());
	
    ResponseFundTransfer responseFundTransfer = new ResponseFundTransfer();
	responseFundTransfer.setMessage("transaction succsess");
	responseFundTransfer.setStatusCode(HttpStatus.CREATED.value());
	return responseFundTransfer;
}



@Transactional(propagation = Propagation.MANDATORY )
public void addAmount(Long id, double amount) throws BankTransactionException {
	
	EmailNotificationService emailNotificationService=new EmailNotificationService();
    Account account = this.getAccount(accountId);
    if (account == null) {
        throw new BankTransactionException("Account not found " + accountId);
    }
    double newBalance = account.getBalance() + amount;
    if (account.getBalance() + amount < 0) {
        throw new BankTransactionException(
                "The money in the account '" + accountId + "' is not enough (" + account.getBalance() + ")");
       
    }
    account.setBalance(newBalance);
    emailNotificationService.notifyAboutTransfer(account, "amount transfered");
   
}
  
  
  
  

}
