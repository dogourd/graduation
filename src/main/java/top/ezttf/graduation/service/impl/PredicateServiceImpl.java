package top.ezttf.graduation.service.impl;

import org.apache.spark.ml.classification.LogisticRegressionModel;
import org.apache.spark.ml.regression.IsotonicRegressionModel;
import org.apache.spark.ml.regression.LinearRegressionModel;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.springframework.stereotype.Service;
import top.ezttf.graduation.service.IPredicateService;

/**
 * @author yuwen
 * @date 2019/4/28
 */
@Service
public class PredicateServiceImpl implements IPredicateService {
    @Override
    public Dataset<Row> isotonicRegressionTrain(
            IsotonicRegressionModel model,
            Dataset<Row> dataSet) {

        return model.transform(dataSet);
    }

    @Override
    public Dataset<Row> linearRegressionTrain(
            LinearRegressionModel model,
            Dataset<Row> dataSet) {
        return model.transform(dataSet);
    }

    @Override
    public Dataset<Row> logisticRegression(
            LogisticRegressionModel model,
            Dataset<Row> dataSet) {
        return model.transform(dataSet);
    }
}
