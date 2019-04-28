package top.ezttf.graduation.service;

import org.apache.spark.ml.classification.LogisticRegressionModel;
import org.apache.spark.ml.regression.IsotonicRegressionModel;
import org.apache.spark.ml.regression.LinearRegressionModel;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

/**
 * @author yuwen
 * @date 2019/4/28
 */
public interface IPredicateService {

    /**
     * 保序回归预测
     *
     * @param model     模型
     * @param dataSet   预测集
     * @return 模型
     */
    Dataset<Row> isotonicRegressionTrain(
            IsotonicRegressionModel model,
            Dataset<Row> dataSet
    );

    /**
     * 线性回归预测
     *
     * @param model     模型
     * @param dataSet   预测集
     * @return 模型
     */
    Dataset<Row> linearRegressionTrain(
            LinearRegressionModel model,
            Dataset<Row> dataSet
    );

    /**
     * 逻辑回归预测
     *
     * @param model     模型
     * @param dataSet   预测集
     * @return 模型
     */
    Dataset<Row> logisticRegression(
            LogisticRegressionModel model,
            Dataset<Row> dataSet
    );
}
