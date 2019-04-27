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
     * 客流用户上一次停留位置: mMac, 模型训练需要  将mMac和数字对应, 唯一的mMac对应唯一的数字
     */
    private Double lastGeo;

    /**
     * 客流用户当前停留位置: mMac, 模型训练需要  将mac和数字对应, 唯一的mac对应唯一的数字
     */
    private Double nowGeo;

    /**
     * 随机数: 用于制作分散数据集
     */
    private Double random;

}
