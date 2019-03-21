package top.ezttf.graduation.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author yuwen
 * @date 2019/3/20
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Wifi {

    /**
     * wifi 在 HBase中的rowKey
     */
    private String rowKey;

    /**
     * 设备 id
     */
    private String id;

    /**
     * 设备mac地址
     */
    private String mmac;

    /**
     * 手机 mac 地址
     */
    private String mac;

    /**
     * 手机和设备距离
     */
    private Double range;

    /**
     * 信息手机时间
     */
    private Date time;
}
