package top.ezttf.graduation.kafka.consumer;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import top.ezttf.graduation.common.Const;
import top.ezttf.graduation.kafka.consumer.handler.ReceiveDataVoHandler;
import top.ezttf.graduation.utils.JsonUtil;
import top.ezttf.graduation.vo.ReceiveData;

import java.util.Optional;

/**
 * @author yuwen
 * @date 2018/11/25
 */
@Component
@Slf4j
public class KafkaConsumer {

    private final ReceiveDataVoHandler voHandler;

    @Autowired
    public KafkaConsumer(ReceiveDataVoHandler voHandler) {
        this.voHandler = voHandler;
    }

    //@KafkaListener(
    //        //id = "flume-listener",
    //        topics = "${project.kafka.topic}",
    //        //clientIdPrefix = "${spring.kafka.consumer.client-id}",
    //        groupId = "${spring.kafka.consumer.group-id}"
    //        //containerFactory = "flumeKafkaListenerContainerFactory"
    //)
    public void consumer(
            ConsumerRecord<?, ?> record
    ) {
        Optional<?> kafkaMessage = Optional.ofNullable(record.value());
        if (kafkaMessage.isPresent()) {
            String message = kafkaMessage.get().toString();
            if (StringUtils.startsWith(message, Const.IKafkaMessageCategory.STANDARD_HEADER)) {
                message = StringUtils.substring(message, Const.IKafkaMessageCategory.STANDARD_HEADER.length());
                ReceiveData vo = JSON.parseObject(message, ReceiveData.class);
                voHandler.handleReceiveDataVo(vo);
                log.error(JsonUtil.obj2StrPretty(vo));
            }
        }
    }
}
