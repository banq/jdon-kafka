package sample.repository;

import com.jdon.annotation.Component;

@Component
public class EventSourceRepository {

	// start kafka consumer
	public EventSourceRepository() {
		String groupId = "test-repl-group";
		String topics = "test-repl";

		ConsumerStreams consumer = new ConsumerStreams(groupId, topics);
		consumer.start();
	}

}
