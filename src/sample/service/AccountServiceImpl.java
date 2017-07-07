package sample.service;

import sample.domain.BankAccount;
import sample.domain.CommandProducerIF;
import sample.domain.TransferEvent;
import sample.repository.AccountRepositoryIF;

import com.jdon.annotation.Service;
import com.jdon.domain.message.DomainMessage;

@Service("accountService")
public class AccountServiceImpl implements AccountService {

	private AccountRepositoryIF accountRepository;
	
	private CommandProducerIF commandProducer;
	
	public AccountServiceImpl(AccountRepositoryIF accountRepository, CommandProducerIF commandProducer) {
		super();
		this.accountRepository = accountRepository;
		this.commandProducer = commandProducer;
	}

	public BankAccount getBankAccount(String id) {
		return accountRepository.getBankAccount(id);
	}
		
	public BankAccount getBankAccount(String id, int amount) {
		return accountRepository.getBankAccount(id, amount);
	}


	public DomainMessage transfer(BankAccount orignal,  BankAccount target, int money) {
		TransferEvent event = new TransferEvent(money, orignal.getId());
		DomainMessage dm = commandProducer.withdrawCommand(orignal, event);
		if (dm.getBlockEventResult() != null){
			boolean isWithdraw = (Boolean)dm.getBlockEventResult();
			if (isWithdraw){
				event = new TransferEvent(money, target.getId());
				dm = commandProducer.depositCommand(target, event);
			}
		}
		return dm;
	}
	
	
}
