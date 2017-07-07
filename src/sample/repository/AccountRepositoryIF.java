package sample.repository;

import sample.domain.BankAccount;

import com.jdon.annotation.pointcut.Around;

public interface AccountRepositoryIF {

	@Around
	public abstract BankAccount getBankAccount(String id);
	

	@Around
	public abstract BankAccount getBankAccount(String id, int amount);

	
	public int loadSequencId();


}