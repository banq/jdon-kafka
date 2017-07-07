package sample.repository;

import java.util.concurrent.atomic.AtomicInteger;

import sample.domain.BankAccount;

import com.jdon.annotation.Component;
import com.jdon.annotation.Introduce;
import com.jdon.annotation.pointcut.Around;


@Component()
@Introduce("modelCache")
public class AccountRepository implements AccountRepositoryIF {
	private final AtomicInteger sequenceId = new AtomicInteger(0);
	
	private final EventSourceRepository eventSourceRepository;
		
	public AccountRepository(EventSourceRepository eventSourceRepository) {
		super();
		this.eventSourceRepository = eventSourceRepository;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jdon.sample.test.cqrs.ABRepositoryIF#loadA(java.lang.String)
	 */
	@Around
	public BankAccount getBankAccount(String id) {
		BankAccount model = new BankAccount(id);		
		return model;

	}
	
	@Around
	public  BankAccount getBankAccount(String id, int amount){
		BankAccount model = new BankAccount(id, amount);		
		return model;
	}
	
	public int loadSequencId(){
		return sequenceId.incrementAndGet();
	}

	public EventSourceRepository getEventSourceRepository() {
		return eventSourceRepository;
	}
	
	


}
