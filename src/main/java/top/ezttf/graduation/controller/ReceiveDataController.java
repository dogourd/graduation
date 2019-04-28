package top.ezttf.graduation.controller;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import top.ezttf.graduation.common.Const;
import top.ezttf.graduation.dao.DeviceRepository;
import top.ezttf.graduation.pojo.Device;
import top.ezttf.graduation.vo.ReceiveData;

import java.util.Map;

/**
 * @author yuwen
 * @date 2018/12/22
 */
@Slf4j
@RestController
public class ReceiveDataController {

    /**
     * 用于记录是否已经保存过mmac地址, 避免不必要的db开销
     */
    private Map<String, String> map = Maps.newHashMap();

    private final DeviceRepository deviceRepository;

    public ReceiveDataController(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    @PostMapping("/dsky")
    public void getData(String data) {
        if (StringUtils.isBlank(data)) {
            return;
        }
        ReceiveData receiveData = JSON.parseObject(data, ReceiveData.class);
        String mMac = receiveData.getMmac();
        // 如若缓存中没有该记录
        if (!map.containsKey(mMac)) {
            Device device = deviceRepository.findByMMac(mMac);
            // 未添加过该记录, 并且数据库也不存在记录, 直接返回不应该记录日志
            if (device == null) {
                return;
            } else {
                map.put(mMac, null);
            }
        }
        log.debug(Const.IKafkaMessageCategory.STANDARD_HEADER + data);
    }



}
