package top.ezttf.graduation.service.impl;

import org.apache.spark.ml.classification.LogisticRegression;
import org.apache.spark.ml.classification.LogisticRegressionModel;
import org.apache.spark.ml.feature.VectorAssembler;
import org.apache.spark.ml.regression.IsotonicRegression;
import org.apache.spark.ml.regression.IsotonicRegressionModel;
import org.apache.spark.ml.regression.LinearRegression;
import org.apache.spark.ml.regression.LinearRegressionModel;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.springframework.stereotype.Service;
import top.ezttf.graduation.service.IRegressionService;

/**
 * @author yuwen
 * @date 2019/4/27
 */
@Service
public class RegressionServiceImpl implements IRegressionService {

    @Override
    public IsotonicRegressionModel isotonicRegressionTrain(Dataset<Row> dataSet, String[] inputCols, String labelCol) {
        VectorAssembler assembler = new VectorAssembler().setInputCols(inputCols).setOutputCol("features");
        Dataset<Row> transform = assembler.transform(dataSet);
        IsotonicRegression regression = new IsotonicRegression().setFeaturesCol("features").setLabelCol(labelCol);
        return regression.fit(dataSet);
    }

    @Override
    public LinearRegressionModel linearRegressionTrain(Dataset<Row> dataSet, String[] inputCols, String labelCol) {
        VectorAssembler assembler = new VectorAssembler().setInputCols(inputCols).setOutputCol("features");
        Dataset<Row> transform = assembler.transform(dataSet);
        LinearRegression regression = new LinearRegression().setFeaturesCol("features").setLabelCol(labelCol);
        return regression.fit(dataSet);
    }

    @Override
    public LogisticRegressionModel logisticRegression(Dataset<Row> dataSet, String[] inputCols, String labelCol) {
        VectorAssembler assembler = new VectorAssembler().setInputCols(inputCols).setOutputCol("features");
        Dataset<Row> transform = assembler.transform(dataSet);
        LogisticRegression regression = new LogisticRegression().setFeaturesCol("features").setLabelCol(labelCol);
        return regression.fit(dataSet);
    }
}
