package top.ezttf.graduation.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import top.ezttf.graduation.common.Const;

import java.util.List;

/**
 * @author yuwen
 * @date 2018/11/25
 */
@Component
@Slf4j
public class KafkaConsumer {

    @KafkaListener(
            id = "flume-listener",
            topics = "${spring.kafka.topic}",
            clientIdPrefix = "${spring.kafka.consumer.client-id}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "flumeKafkaListenerContainerFactory"
    )
    public void consumer(
            List<String> str
    ) {
        for (String s : str) {
            if (StringUtils.startsWith(s, Const.IKafkaMessageCategory.STANDARD_HEADER)) {
                log.error(StringUtils.substring(s, Const.IKafkaMessageCategory.STANDARD_HEADER.length()));
            }
        }
        //for (ConsumerRecord<String, String> record : recordList) {
        //    log.error(record.value());
        //}
        //acknowledgment.acknowledge();

        //WifiEntity entity = JsonUtil.str2Obj(content, WifiEntity.class);
        //if (entity != null) {
        //    log.debug(entity.toString());
        //}
        //log.error(content + "被消费");
    }

}
