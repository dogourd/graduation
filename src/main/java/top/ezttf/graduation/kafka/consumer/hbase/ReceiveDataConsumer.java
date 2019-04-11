package top.ezttf.graduation.kafka.consumer.hbase;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.stereotype.Component;
import top.ezttf.graduation.common.Const;
import top.ezttf.graduation.service.IWarnService;
import top.ezttf.graduation.service.IWifiService;
import top.ezttf.graduation.vo.ReceiveData;
import top.ezttf.graduation.vo.Warn;
import top.ezttf.graduation.vo.Wifi;

import java.util.List;
import java.util.Optional;


/**
 * @author yuwen
 * @date 2018/11/25
 */
@Slf4j
@Component
public class ReceiveDataConsumer {


    private final IWarnService iWarnService;

    private final IWifiService iWifiService;

    public ReceiveDataConsumer(IWarnService iWarnService, IWifiService iWifiService) {
        this.iWarnService = iWarnService;
        this.iWifiService = iWifiService;
    }

    /**
     * 将探针接收到的数据转为{@link ReceiveData}, 提取{@link Warn}和{@link Wifi}存入Hbase中
     * @param record
     */
    // TODO 放开注释
    //@KafkaListener(
    //        //id = "flume-listener",
    //        topics = "${project.kafka.flume-topic}",
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

                List<Wifi> wifis = Wifi.createWifisFromData(vo);
                Warn warn = Warn.assembleFromReceiveData(vo);
                iWarnService.saveWarn(warn);
                iWifiService.saveWifis(wifis);
            }
        }
    }


    public void sparkCompute(ConsumerRecord<?, ?> record) {
        Optional<?> kafkaMessage = Optional.ofNullable(record.value());

    }
}
