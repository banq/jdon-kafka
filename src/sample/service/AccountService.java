package sample.service;

import sample.domain.BankAccount;

import com.jdon.domain.message.DomainMessage;

public interface AccountService {

	public DomainMessage transfer(BankAccount orignal, BankAccount target, int money);
	
	BankAccount getBankAccount(String id);
	
	BankAccount getBankAccount(String id, int amount); 
	
}
