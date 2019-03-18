package top.ezttf.graduation.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import top.ezttf.graduation.common.Const;

/**
 * @author yuwen
 * @date 2018/12/22
 */
@Slf4j
@RestController
public class ReceiveData {

    @PostMapping("/dsky")
    public void getData(String data) {
        if (data != null) {
            log.debug(Const.IKafkaMessageCategory.STANDARD_HEADER + data);
        }
        //log.debug(data);
        //ReceiveDataVo vo = JSON.parseObject(data, ReceiveDataVo.class);
        //log.debug(JsonUtil.obj2StrPretty(vo));
    }


    /**
     * id: 设备id
     * lat: 维度
     * lon: 经度
     * rate: 发送频率
     * mmac: 设备自身mac地址
     * time: 数据收集的时间戳
     *
     * tmc: 手机所连接的wifi的mac地址
     * mac: 采集到的手机mac地址
     * rssi:  手机信号强度
     * range: 距离
     * ch: 信道
     * ts: 手机连接的wifi的ssid
     * tc: 是否与路由器相连
     * ds: 手机是否睡眠
     * essidx: 历史连接的wifi ssid
     */
}
