package top.ezttf.graduation.service;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import java.util.List;

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
    Dataset<Row> predicateWarn();

    /**
     * 预测wifi
     * @param lastGeo 地理位置代表的序号: 如建院(2L)
     * @return mMac对应的ids
     */
    List<Long> predicateWifi(double lastGeo);
}
