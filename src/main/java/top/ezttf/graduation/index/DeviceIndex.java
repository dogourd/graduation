package top.ezttf.graduation.index;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yuwen
 * @date 2019/4/27
 */
@Slf4j
@Component
public class DeviceIndex implements IIndexAware<String, Long> {

    private ConcurrentHashMap<String, Long> map = new ConcurrentHashMap<>();

    @Override
    public Long get(String key) {
        log.info("DeviceIndex, the key set is {}", map);
        return map.get(key);
    }

    /**
     * nMac 2 primary key
     * @param key Key
     * @param value value
     */
    @Override
    public void add(String key, Long value) {
        log.info("DeviceIndex, before add the key set is {}", map.keySet());
        map.put(key, value);
        log.info("DeviceIndex, after add the key set is {}", map.keySet());
    }

    @Override
    public void update(String key, Long value) {
        log.error("not support update DeviceIndex, the value must be the primary key");
    }

    @Override
    public void delete(String key, Long value) {
        log.info("DeviceIndex, before delete the key set is {}", map.keySet());
        map.remove(key);
        log.info("DeviceIndex, after delete the key set is {}", map.keySet());
    }

    public List<Long> getIds() {
        return Lists.newArrayList(map.values());
    }

    public String getMMacById(long id) {
        for (Map.Entry<String, Long> entry : map.entrySet()) {
            if (entry.getValue() == id) {
                return entry.getKey();
            }
        }
        return null;
    }
}
