package sample.domain;


public class WithdrawEvent extends TransferEvent {

	public WithdrawEvent(int value, String aggreId) {
		super( value, aggreId);
	}

}
