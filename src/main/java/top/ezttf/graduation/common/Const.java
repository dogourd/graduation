package top.ezttf.graduation.common;

/**
 * 常量类
 *
 * @author yuwen
 * @date 2018/12/26
 */
public class Const {

    /**
     * 使用消息头区分 Kafka消息
     */
    public interface IKafkaMessageCategory {
        String STANDARD_HEADER = "flume-kafka-message";
    }
}
