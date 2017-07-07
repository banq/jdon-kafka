package sample.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;

import sample.repository.mysql.SimpleDateSource;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ConsumerLoop implements Runnable {

	private final KafkaConsumer<String, JsonNode> consumer;
	private final List<String> topics;
	private final int id;
	private final SimpleDateSource simpleDateSource;

	public ConsumerLoop(int id, String groupId, List<String> topics) {
		this.simpleDateSource = SimpleDateSource.instance();
		this.id = id;
		this.topics = topics;
		Properties props = new Properties();
		props.put("bootstrap.servers", "localhost:9092");
		props.put("group.id", groupId);
		props.put("enable.auto.commit", "false");
		props.put("auto.commit.interval.ms", "1000");
		props.put("session.timeout.ms", "30000");
		props.put("key.deserializer", StringDeserializer.class.getName());
		props.put("value.deserializer",
				"org.apache.kafka.connect.json.JsonDeserializer");

		this.consumer = new KafkaConsumer<>(props);
	}

	@Override
	public void run() {
		try {
			consumer.subscribe(topics, new MyConsumerRebalancerListener(
					consumer));

			processRecords(consumer);
		} catch (Exception e) {
			// ignore for shutdown
		} finally {
			consumer.close();
		}
	}

	public void shutdown() {
		consumer.wakeup();
	}

	public static void main(String[] args) {
		int numConsumers = 3;
		String groupId = "test-repl-group";
		List<String> topics = Arrays.asList("test-repl");
		final ExecutorService executor = Executors
				.newFixedThreadPool(numConsumers);

		final List<ConsumerLoop> consumers = new ArrayList<>();
		for (int i = 0; i < numConsumers; i++) {
			ConsumerLoop consumer = new ConsumerLoop(i, groupId, topics);
			consumers.add(consumer);
			executor.submit(consumer);
		}

		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				for (ConsumerLoop consumer : consumers) {
					consumer.shutdown();
				}
				executor.shutdown();
				try {
					executor.awaitTermination(5000, TimeUnit.MILLISECONDS);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
	}

	private void processRecords(KafkaConsumer<String, JsonNode> consumer) {
		ObjectMapper mapper = new ObjectMapper();
		while (true) {
			ConsumerRecords<String, JsonNode> records = consumer
					.poll(Long.MAX_VALUE);
			for (ConsumerRecord<String, JsonNode> record : records) {
				int pid = record.partition();
				System.out.println(pid + "=" + record.offset() + ": "
						+ record.value());
				JsonNode jsonNode = record.value();
				Connection connection = null;
				try {
					TransferEventDTO event = (TransferEventDTO) mapper
							.treeToValue(jsonNode, TransferEventDTO.class);
					System.out.println("event" + event.getId()
							+ event.getAggreRootId());

					long lastoffset = record.offset();
					connection = simpleDateSource.getConnection();
					connection = simpleDateSource.getConnection();
					connection.setAutoCommit(false);
					String sql = "insert into transferevent (eventId,aggreId,value,offset) values(?,?,?,?)";
					PreparedStatement pstmt = (PreparedStatement) connection
							.prepareStatement(sql);
					pstmt.setLong(1, event.getId());
					pstmt.setString(2, event.getAggreRootId());
					pstmt.setInt(3, event.getValue());
					pstmt.setLong(4, lastoffset);
					pstmt.executeUpdate();
					pstmt.close();

					String sql2 = "update kafkaoffset set offset=? where pid=?";
					pstmt = (PreparedStatement) connection
							.prepareStatement(sql2);
					pstmt.setLong(1, lastoffset);
					pstmt.setLong(2, pid);
					pstmt.executeUpdate();

					Map<TopicPartition, OffsetAndMetadata> offsets = Collections
							.singletonMap(new TopicPartition(record.topic(),
									pid), new OffsetAndMetadata(
									record.offset() + 1));
					consumer.commitSync(offsets);
//					 throw new Exception();
					connection.commit();

				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					try {
						connection.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}

			}
		}
		// }

	}

}
