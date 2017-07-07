package sample.repository;


public class TransferEventDTO {

	private long id;
	private int value;
	private String aggreRootId;

	public String getAggreRootId() {
		return aggreRootId;
	}

	public void setAggreRootId(String aggreRootId) {
		this.aggreRootId = aggreRootId;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
	

}
