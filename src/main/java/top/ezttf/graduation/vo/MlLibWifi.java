package top.ezttf.graduation.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

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
     * 随机数, 用于建立分散的训练数据集
     */
    private Double random;

    public MlLibWifi(String mac, String mMac) {
        this.mac = mac;
        this.mMac = mMac;
    }
}
