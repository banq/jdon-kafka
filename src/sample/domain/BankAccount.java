package sample.domain;

import java.util.ArrayList;
import java.util.List;

import com.jdon.annotation.Model;
import com.jdon.annotation.model.Inject;
import com.jdon.annotation.model.OnCommand;

@Model
public class BankAccount {

	private final String id;

	private int amount = 0;

	private List<TransferEvent> eventsources = new ArrayList<TransferEvent>();

	@Inject
	private DomainEventProducer domainEventProducer;

	public BankAccount(String id) {
		super();
		this.id = id;
	}

	public BankAccount(String id, int amount) {
		super();
		this.id = id;
		this.amount = amount;
	}

	@OnCommand("depositCommand")
	public boolean deposit(TransferEvent transferEvent) {
		DepositEvent transferEventNew = new DepositEvent(
				transferEvent.getValue(), this.id);
		eventsources.add(transferEventNew);
		domainEventProducer.transfer(transferEventNew);
		play();
		if (getAmount() > 1000) {
			DepositEventFail transferEventFNew = new DepositEventFail(
					transferEvent.getValue(), this.id);
			eventsources.add(transferEventFNew);
			domainEventProducer.transfer(transferEventFNew);
			play();
			return false;
		}
		return true;
	}

	@OnCommand("withdrawCommand")
	public boolean withdraw(TransferEvent transferEvent) {
		WithdrawEvent transferEventNew = new WithdrawEvent(
				transferEvent.getValue(), this.id);
		eventsources.add(transferEventNew);
		domainEventProducer.transfer(transferEventNew);
		play();

		if (getAmount() < 0) {
			WithdrawEventFail transferEventFNew = new WithdrawEventFail(
					transferEvent.getValue(), this.id);
			eventsources.add(transferEventFNew);
			domainEventProducer.transfer(transferEventFNew);
			play();
			return false;
		}
		return true;

	}

	public String getId() {
		return id;
	}

	public int play() {
		for (TransferEvent tevent : eventsources) {
			if (tevent instanceof DepositEvent)
				amount = amount + tevent.getValue();
			else if (tevent instanceof DepositEventFail)
				amount = amount - tevent.getValue();
			else if (tevent instanceof WithdrawEvent)
				amount = amount - tevent.getValue();
			else if (tevent instanceof WithdrawEventFail)
				amount = amount + tevent.getValue();
		}
		return amount;
	}

	public int getAmount() {
		return amount;
	}

}
