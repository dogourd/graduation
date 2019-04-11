package top.ezttf.graduation.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.utils.DateUtils;
import top.ezttf.graduation.vo.Warn;
import top.ezttf.graduation.vo.Wifi;

import java.time.Instant;
import java.util.StringJoiner;

/**
 * @author yuwen
 * @date 2019/3/20
 */
@Slf4j
public class RowKeyGenUtil {


    /**
     * 根据提供的 wifi 对象生成 rowKey (用户 mac 地址)
     * @param wifi
     * @return
     */
    public static String genWifiRowKey(Wifi wifi) {
        String rowKey = new StringJoiner("-")
                .add(wifi.getMac())
                .add(DateUtils.formatDate(wifi.getTime()))
                .toString();
        log.info("GenWifiRowKey: {}, {}", wifi, rowKey);
        return rowKey;
    }

    /**
     * 根据提供的 warn 对象生成 rowKey ("mmac-time")
     * @param warn
     * @return
     */
    public static String genWarnRowKey(Warn warn) {
        String rowKey = new StringJoiner("-")
                .add(warn.getMmac())
                .add(Instant.now().toString())
                .toString();
        log.info("GenWarnRowKey: {}, {}", warn, rowKey);
        return rowKey;
    }
}
