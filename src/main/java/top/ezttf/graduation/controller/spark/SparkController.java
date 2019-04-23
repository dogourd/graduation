package top.ezttf.graduation.controller.spark;

import com.spring4all.spring.boot.starter.hbase.api.HbaseTemplate;
import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableInputFormat;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.VoidFunction;
import org.apache.spark.ml.feature.VectorAssembler;
import org.apache.spark.ml.regression.IsotonicRegression;
import org.apache.spark.ml.regression.IsotonicRegressionModel;
import org.apache.spark.rdd.RDD;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import scala.Tuple2;
import top.ezttf.graduation.constant.Constants;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Pattern;

/**
 * @author yuwen
 * @date 2019/3/18
 */
@Slf4j
@RestController
public class SparkController {

    private static final Pattern pattern = Pattern.compile(" ");

    private final HbaseTemplate hbaseTemplate;

    public SparkController(HbaseTemplate hbaseTemplate) {
        this.hbaseTemplate = hbaseTemplate;
    }

    @GetMapping("wordCount")
    public void wordCount() throws IOException {
        BufferedWriter writer = Files.newBufferedWriter(Paths.get("/home/yuwen/result.txt"));
        SparkSession spark = new SparkSession(new SparkContext(
                new SparkConf().setMaster("local[2]")
                        .set("spark.executor.memory", "512m")
                        .setAppName("wordCount")
        ));

        spark.read()
                .textFile("hdfs://hadoop:8020/README.txt").javaRDD()
                .flatMap(word -> Arrays.asList(pattern.split(word)).iterator())
                .mapToPair(word -> new Tuple2<>(word, 1))
                .reduceByKey((i, j) -> i + j)
                .collect()
                .forEach(out -> {
                    try {
                        writer.write(out._1() + ": " + out._2());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
        writer.close();
        spark.stop();
    }


    /**
     * 处理warn
     * TODO 做成定时任务
     *
     * @return
     * @throws InterruptedException
     */
    @GetMapping("/readHbase")
    public String readHbase() throws InterruptedException {
        SparkConf sparkConf = new SparkConf().setAppName("readHbase")
                .setMaster("local[2]")
                .set("spark.executor.memory", "512m");
        SparkContext sparkContext = new SparkContext(sparkConf);
        Configuration configuration = hbaseTemplate.getConfiguration();
        configuration.set(TableInputFormat.INPUT_TABLE, Constants.WarnTable.TABLE_NAME);
        RDD<Tuple2<ImmutableBytesWritable, Result>> hbaseRDD =
                sparkContext.newAPIHadoopRDD(
                        configuration,
                        TableInputFormat.class,
                        ImmutableBytesWritable.class,
                        Result.class
                );
        JavaRDD<Tuple2<ImmutableBytesWritable, Result>> javaRDD = hbaseRDD.toJavaRDD();
        javaRDD.foreach((VoidFunction<Tuple2<ImmutableBytesWritable, Result>>) immutableBytesWritableResultTuple2 -> {
            Result result = immutableBytesWritableResultTuple2._2();
            // 行键
            String key = Bytes.toString(result.getRow());
            // 设置id
            String id = Bytes.toString(result.getValue(
                    Constants.WarnTable.FAMILY_D.getBytes(),
                    Constants.WarnTable.ID.getBytes()
            ));
            // 设备mac地址
            String mmac = Bytes.toString(result.getValue(
                    Constants.WarnTable.FAMILY_D.getBytes(),
                    Constants.WarnTable.MMAC.getBytes()
            ));
            // 处理的本批数据的人数
            long count = Bytes.toLong(result.getValue(
                    Constants.WarnTable.FAMILY_I.getBytes(),
                    Constants.WarnTable.COUNT.getBytes())
            );
            // 时间
            String time = Bytes.toString(result.getValue(
                    Constants.WarnTable.FAMILY_T.getBytes(),
                    Constants.WarnTable.TIME.getBytes()
            ));
            log.debug("{}, {}, {}, {}", count, id, mmac, time);
        });

        sparkContext.stop();
        return null;
    }


    /**
     * 测试spark 线性回归
     *
     * @return
     */
    @GetMapping("/line")
    public String linearRegression() {
        SparkConf sparkConf = new SparkConf()
                .setAppName("linear")
                .setMaster("local[2]")
                .set("spark.executor.memory", "512m");
        JavaSparkContext sparkContext = new JavaSparkContext(sparkConf);
        sparkContext.setLogLevel("WARN");
        Configuration configuration = hbaseTemplate.getConfiguration();
        configuration.set(TableInputFormat.INPUT_TABLE, Constants.WarnTable.TABLE_NAME);


        JavaPairRDD<ImmutableBytesWritable, Result> hbaseRDD = sparkContext.newAPIHadoopRDD(
                configuration,
                TableInputFormat.class,
                ImmutableBytesWritable.class,
                Result.class
        );
        ThreadLocalRandom random = ThreadLocalRandom.current();
        JavaRDD<Temp> javaRDD = hbaseRDD.map((Function<Tuple2<ImmutableBytesWritable, Result>, Temp>) v1 -> {
            Result result = v1._2();
            String t = Bytes.toString(result.getValue(
                    Constants.WarnTable.FAMILY_T.getBytes(),
                    Constants.WarnTable.TIME.getBytes()
            ));
            long time = Timestamp.valueOf(t).getTime();
            long count = Bytes.toLong(result.getValue(
                    Constants.WarnTable.FAMILY_I.getBytes(),
                    Constants.WarnTable.COUNT.getBytes()
            ));
            return new Temp((double) time, (double) count, random.nextDouble());
        });


        SparkSession sparkSession = SparkSession.builder().sparkContext(sparkContext.sc()).getOrCreate();
        Dataset<Row> dataset = sparkSession.createDataFrame(javaRDD, Temp.class);
        dataset = dataset.sort("random");
//        dataset = dataset.toDF("count, time");
//        dataset.show();
        dataset.randomSplit(new double[]{0.8, 0.2});


        VectorAssembler assembler = new VectorAssembler().setInputCols(new String[]{"time"}).setOutputCol("features");
        Dataset<Row> transform = assembler.transform(dataset);
        Dataset<Row>[] datasets = transform.randomSplit(new double[]{0.8, 0.2});

//        datasets[0].show((int) datasets[0].count());
//        datasets[1].show((int) datasets[1].count());

        // FIXME 保序回归
        IsotonicRegression isotonicRegression = new IsotonicRegression().setFeaturesCol("features").setLabelCol("count");
        IsotonicRegressionModel isotonicRegressionModel = isotonicRegression.fit(datasets[0]);
        isotonicRegressionModel.transform(datasets[1]).show();

        log.warn("=========================================");
        log.warn("=========================================");
        log.warn("=========================================");

        // FIXME 线性回归
//        LinearRegression linearRegression = new LinearRegression().setMaxIter(10).setRegParam(0.3).setElasticNetParam(0.8);
//        linearRegression.setFeaturesCol("features").setLabelCol("count");
//        LinearRegressionModel linearRegressionModel = linearRegression.fit(datasets[0]);
//        linearRegressionModel.transform(datasets[1]).show();

        log.warn("=========================================");
        log.warn("=========================================");
        log.warn("=========================================");

        // FIXME 逻辑回归
//        LogisticRegression logisticRegression = new LogisticRegression()
//                .setFeaturesCol("features")
//                .setLabelCol("count")
//                .setRegParam(0.3)
//                .setElasticNetParam(0.8)
//                .setMaxIter(10);
//        LogisticRegressionModel logisticRegressionModel = logisticRegression.fit(datasets[0]);
//        Dataset<Row> logisticResult = logisticRegressionModel.transform(datasets[1]);
//        List<Row> prediction = logisticResult.select("prediction").toJavaRDD().collect();
//        prediction.forEach(row -> {
//            Object o = row.get(0);
//            log.debug("{}", o);
//        });
//        logisticResult.show();

        return "finish...";

    }


}
