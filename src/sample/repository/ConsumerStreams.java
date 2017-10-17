package sample.repository;

import java.util.Properties;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.ForeachAction;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KStreamBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ConsumerStreams {

	private final String groupId;
	private final String topic;
	private final ObjectMapper mapper = new ObjectMapper();

	public ConsumerStreams(String groupId, String topic) {
		this.groupId = groupId;
		this.topic = topic;
	}

	public void start() {
		// Before you start I would always suggest reading a bit on the topic.
		// That way you are not completely confused when things go bad.
		// Here is a few links to some help information on this topic:
		// https://cwiki.apache.org/confluence/display/KAFKA/KIP-129%3A+Streams+Exactly-Once+Semantics
		// https://kafka.apache.org/0110/documentation/streams/quickstart

		// Get the properties of the Kafka Stream. This is where you can set the
		// individual properties for the stream. How many bytes to accept... so
		// on.
		Properties streamsProperties = getProperties();

		// String serializers and deserializers for the Kafka records.
		Serde<String> stringSerde = Serdes.String();

		// Kafka stream builder. What builds the stream we are creating
		KStreamBuilder builder = new KStreamBuilder();

		// Get all the records from the defined topic that haven't been consumed
		// or committed.
		KStream<String, String> records = builder.stream(stringSerde,
				stringSerde, topic);

		// Do something with records
		// For this example we will forward each record to an endpoint. Using a
		// lambda expression for java8
		records.foreach(new ForeachAction<String, String>() {
			@Override
			public void apply(String key, String textLine) {
				System.out.println(key + " => " + textLine);
				try {
					JsonNode jsonNode = mapper.readTree(textLine);
					TransferEventDTO event = (TransferEventDTO) mapper
							.treeToValue(jsonNode, TransferEventDTO.class);
					System.out.println("event" + event.getId()
							+ event.getAggreRootId());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		// Kafka Steam its self
		KafkaStreams stream = new KafkaStreams(builder, streamsProperties);

		// Start the stream.
		stream.start();

	}

	private Properties getProperties() {
		Properties props = new Properties();

		props.put(StreamsConfig.APPLICATION_ID_CONFIG, groupId);
		// props.put("application.id", "kafka-streams-example");

		props.put(StreamsConfig.CLIENT_ID_CONFIG, this.getClass().getClass()
				.getName());
		// props.put("client.id", "kafka-streams-example-client");

		// props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "0.0.0.0:9092");
		// // props.put("bootstrap.servers", "0.0.0.0:9092");
		props.put("bootstrap.servers",
				"192.168.17.117:9092,192.168.17.118:9092,192.168.17.119:9092");

		props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String()
				.getClass().getName());
		// props.put("default.key.serde", Serdes.String().getClass().getName());

		props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes
				.String().getClass().getName());
		// props.put("default.value.serde",
		// Serdes.String().getClass().getName());

		// props.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 100);
		// // props.put("commit.interval.ms", 100);
		//
		// props.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, 0);
		// // props.put("cache.max.bytes.buffering", 0);

		// The magic. Set the stream to use exactly-once semantics rather than
		// at-least-once..
		props.put(StreamsConfig.PROCESSING_GUARANTEE_CONFIG,
				StreamsConfig.EXACTLY_ONCE);
		// props.put("processing.guarantee", "exactly_once");

		return props;

	}

	public static void main(String[] args) {

		ConsumerStreams consumerLoop = new ConsumerStreams("test-id", "test-repl");
		consumerLoop.start();
	}

}
