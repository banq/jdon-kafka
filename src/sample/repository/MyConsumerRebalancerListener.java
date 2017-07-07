package sample.repository;

import java.util.Collection;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.common.TopicPartition;

import sample.repository.mysql.OffsetManager;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Re-balancer for any subscription changes.
 */
public class MyConsumerRebalancerListener implements org.apache.kafka.clients.consumer.ConsumerRebalanceListener {

    private OffsetManager offsetManager = new OffsetManager();
    private Consumer<String, JsonNode> consumer;

    public MyConsumerRebalancerListener(Consumer<String, JsonNode> consumer) {
        this.consumer = consumer;
    }

    public void onPartitionsRevoked(Collection<TopicPartition> partitions) {

        for (TopicPartition partition : partitions) {

            offsetManager.saveOffsetInExternalStore(partition.topic(), partition.partition(), consumer.position(partition));
        }
    }

    public void onPartitionsAssigned(Collection<TopicPartition> partitions) {


        for (TopicPartition partition : partitions) {
            consumer.seek(partition, offsetManager.readOffsetFromExternalStore(partition.topic(), partition.partition())+1);
        }
    }


}
