package sample.domain;

import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jdon.annotation.Component;
import com.jdon.annotation.model.OnEvent;

@Component()
public class DomainEventConsumer {
	Properties props;

	public DomainEventConsumer() {
		props = new Properties();
		props.put("bootstrap.servers", "localhost:9092");
		props.put("acks", "all");
		props.put("retries", 0);
		props.put("batch.size", 16384);
		props.put("linger.ms", 1);
		props.put("buffer.memory", 33554432);
		props.put("key.serializer",
				"org.apache.kafka.common.serialization.StringSerializer");
		props.put("value.serializer",
				"org.apache.kafka.connect.json.JsonSerializer");
	}

	@OnEvent("transfer")
	public void send2Kafka(TransferEvent event) {
		try {
			Producer<String, JsonNode> producer = new KafkaProducer<>(props);
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode jsonNode = objectMapper.valueToTree(event);
			producer.send(new ProducerRecord<String, JsonNode>("test-repl",
					jsonNode));
			producer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		DomainEventConsumer domainEventConsumer = new DomainEventConsumer();
		domainEventConsumer.send2Kafka(new TransferEvent(100,"11"));
		
	}

}
