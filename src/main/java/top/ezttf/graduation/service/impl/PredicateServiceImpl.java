package top.ezttf.graduation.service.impl;

import org.apache.spark.ml.classification.LogisticRegressionModel;
import org.apache.spark.ml.feature.VectorAssembler;
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
    public Dataset<Row> isotonicRegressionPredicate(
            IsotonicRegressionModel model,
            Dataset<Row> dataSet,
            String[] inputCols,
            String features) {
        VectorAssembler assembler = new VectorAssembler().setInputCols(inputCols).setOutputCol(features);
        Dataset<Row> transform = assembler.transform(dataSet);
        return model.transform(transform);
    }

    @Override
    public Dataset<Row> linearRegressionPredicate(
            LinearRegressionModel model,
            Dataset<Row> dataSet,
            String[] inputCols,
            String features) {
        VectorAssembler assembler = new VectorAssembler().setInputCols(inputCols).setOutputCol(features);
        Dataset<Row> transform = assembler.transform(dataSet);
        return model.transform(dataSet);
    }

    @Override
    public Dataset<Row> logisticRegressionPredicate(
            LogisticRegressionModel model,
            Dataset<Row> dataSet,
            String[] inputCols,
            String features) {
        VectorAssembler assembler = new VectorAssembler().setInputCols(inputCols).setOutputCol(features);
        Dataset<Row> transform = assembler.transform(dataSet);
        return assembler.transform(dataSet);
    }
}
