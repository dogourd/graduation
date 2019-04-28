package top.ezttf.graduation.service;

import org.apache.spark.ml.classification.LogisticRegressionModel;
import org.apache.spark.ml.regression.IsotonicRegressionModel;
import org.apache.spark.ml.regression.LinearRegressionModel;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

/**
 * @author yuwen
 * @date 2019/4/27
 */
public interface IRegressionService {

    /**
     * 保序回归训练
     * @param dataSet 训练集
     * @param inputCols 自变量列
     * @param features 中间关系列
     * @return 模型
     */
    IsotonicRegressionModel isotonicRegressionTrain(Dataset<Row> dataSet, String[] inputCols, String features);

    /**
     * 线性回归训练
     * @param dataSet 训练集
     * @param inputCols 自变量列
     * @param features 中间关系列
     * @return 模型
     */
    LinearRegressionModel linearRegressionTrain(Dataset<Row> dataSet, String[] inputCols, String features);

    /**
     * 逻辑回归训练
     * @param dataSet 训练集
     * @param inputCols 自变量列
     * @param features 中间关系列
     * @return 模型
     */
    LogisticRegressionModel logisticRegression(Dataset<Row> dataSet, String[] inputCols, String features);


}
