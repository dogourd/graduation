package top.ezttf.graduation.service;

import java.util.List;
import java.util.Map;

/**
 * @author yuwen
 * @date 2019/4/28
 */
public interface ISparkService {

    /**
     * 训练warn数据
     */
    void trainWarn();

    /**
     * 训练wifi数据
     */
    void trainWifi();

    /**
     * 预测warn
     * @return dataset
     */
    Map<String, Double> predicateWarn();

    /**
     * 预测wifi
     * @param lastGeo 地理位置代表的序号: 如建院(2L)
     * @return mMac对应的ids
     */
    List<Long> predicateWifi(double lastGeo);
}
