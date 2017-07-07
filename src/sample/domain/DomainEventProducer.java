package sample.domain;

import com.jdon.annotation.Introduce;
import com.jdon.annotation.model.Send;
import com.jdon.domain.message.DomainMessage;



@Introduce("message")
public class DomainEventProducer {

	@Send("transfer")
	public DomainMessage  transfer(TransferEvent event) {
		return new DomainMessage (event);
	}

	
}
