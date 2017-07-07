package sample.domain;

public class DepositEvent extends TransferEvent{

	public DepositEvent(int value, String aggreId) {
		super(value, aggreId);
	}

}
