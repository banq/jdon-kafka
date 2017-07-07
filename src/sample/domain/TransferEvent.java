package sample.domain;


public class TransferEvent {

	private final long id;
	private final int value;
	private final String aggreRootId;
	
	public TransferEvent(int value, String aggreRootId) {
		super();
		this.id = System.currentTimeMillis();
		this.value = value;
		this.aggreRootId = aggreRootId;
	}

	public long getId() {
		return id;
	}

	public int getValue() {
		return value;
	}

	public String getAggreRootId() {
		return aggreRootId;
	}
	
	

	
}
