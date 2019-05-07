package top.ezttf.graduation.kafka;

import lombok.extern.slf4j.Slf4j;

/**
 * @author yuwen
 * @date 2019/3/19
 */
@Slf4j
public class Mid {

    public static void main(String[] args) {
        log.debug("{}", "AaAa".hashCode());
        log.debug("{}", "BBBB".hashCode());
        //Map<String, String> map = new ConcurrentHashMap<>();
        //map.computeIfAbsent("AaAa", key -> map.put("BBBB", "value"));

    }
}
