package top.ezttf.graduation.kafka.producer;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import top.ezttf.graduation.vo.Warn;

/**
 * @author yuwen
 * @date 2019/3/6
 */
@Component
public class WarnProducer {

    @Value("${project.kafka.warn-topic}")
    private String topic;

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    public WarnProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void warnSend(Warn warn) {
        kafkaTemplate.send(topic, JSON.toJSONString(warn));
    }

}
