package top.ezttf.graduation.config.kafka;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;

import java.lang.String;

import java.util.HashMap;
import java.util.Map;

/**
 * Kafka 相关 bean 配置
 *
 * @author yuwen
 * @date 2018/12/26
 */
@Configuration
public class KafkaConfiguration {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.consumer.enable-auto-commit}")
    private Boolean autoCommit;

    @Value("${spring.kafka.producer.retries}")
    private Integer retries;

    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

    @Value("${spring.kafka.consumer.client-id}")
    private String clientId;

    @Value("${spring.kafka.consumer.auto-offset-reset}")
    private String offset;


    /**
     * consumer
     *
     * @return
     */
    @Bean(name = "flumeKafkaListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, String> flumeKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> listenerContainerFactory =
                new ConcurrentKafkaListenerContainerFactory<>();
        listenerContainerFactory.setConsumerFactory(flumeKafkaConsumerFactory());
        listenerContainerFactory.setBatchListener(true);
        listenerContainerFactory.getContainerProperties().setPollTimeout(3 * 1000);
        listenerContainerFactory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        listenerContainerFactory.setAutoStartup(true);

        return listenerContainerFactory;
    }

    private ConsumerFactory<String, String> flumeKafkaConsumerFactory() {
        return new DefaultKafkaConsumerFactory<>(
                flumeKafkaConsumerFactoryConfigs(),
                new StringDeserializer(),
                new StringDeserializer()
        );
    }

    private Map<String, Object> flumeKafkaConsumerFactoryConfigs() {
        HashMap<String, Object> map = new HashMap<>(7 * 4 / 3 + 1);
        map.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        map.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, autoCommit);
        map.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        map.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        map.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        map.put(ConsumerConfig.CLIENT_ID_CONFIG, clientId);
        map.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, offset);

        return map;
    }
}
