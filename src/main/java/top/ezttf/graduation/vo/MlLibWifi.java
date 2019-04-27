package top.ezttf.graduation.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * @author yuwen
 * @date 2019/4/27
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MlLibWifi implements Serializable {

    /**
     * 客流用户mac地址: mac
     */
    private String mac;

    /**
     * 客流用户当前停留位置: mMac
     */
    private String mMac;

    /**
     * 时间, 用于初始化排序获取位置因果关系
     */
    private Date date;

    /**
     * 随机数用于分散训练集
     */
    private Double random;

    public MlLibWifi(String mac, String mMac) {
        this.mac = mac;
        this.mMac = mMac;
    }
}
