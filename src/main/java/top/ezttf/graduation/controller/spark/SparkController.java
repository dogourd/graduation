package top.ezttf.graduation.controller.spark;

import com.google.common.collect.Lists;
import com.spring4all.spring.boot.starter.hbase.api.HbaseTemplate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
import org.apache.spark.sql.RelationalGroupedDataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import scala.Tuple2;
import top.ezttf.graduation.constant.Constants;
import top.ezttf.graduation.vo.MlLibWarn;
import top.ezttf.graduation.vo.MlLibWifi;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Pattern;

/**
 * @author yuwen
 * @date 2019/3/18
 */
@Slf4j
@RestController
public class SparkController {

//    JavaSparkContext sparkContext = new JavaSparkContext(
//            new SparkConf().setMaster("local[2]")
//                    .setAppName("predictionWarn")
//                    .set("spark.executor.memory", "512m")
//    );

    private static final Pattern pattern = Pattern.compile(" ");
    private static IsotonicRegressionModel warnModel;
    private static IsotonicRegressionModel wifiModel;


    private final HbaseTemplate hbaseTemplate;
    private final JavaSparkContext sparkContext;


    public SparkController(HbaseTemplate hbaseTemplate, JavaSparkContext sparkContext) {
        this.hbaseTemplate = hbaseTemplate;
        this.sparkContext = sparkContext;
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
     * 测试spark mllib回归
     * 包含: 保序回归、线性回归、逻辑回归
     *
     * 该方法训练的数据来源于hbase数据表'graduation:warn'
     * @return
     */
    @GetMapping("/trainWarn")
    public String trainWarn() {
//        sparkContext.setLogLevel("WARN");
        Configuration configuration = hbaseTemplate.getConfiguration();
        configuration.set(TableInputFormat.INPUT_TABLE, Constants.WarnTable.TABLE_NAME);


        JavaPairRDD<ImmutableBytesWritable, Result> hbaseRDD = sparkContext.newAPIHadoopRDD(
                configuration,
                TableInputFormat.class,
                ImmutableBytesWritable.class,
                Result.class
        );
        ThreadLocalRandom random = ThreadLocalRandom.current();
        JavaRDD<MlLibWarn> javaRDD = hbaseRDD.map((Function<Tuple2<ImmutableBytesWritable, Result>, MlLibWarn>) v1 -> {
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
            return new MlLibWarn((double) time, (double) count, random.nextDouble());
        });


        SparkSession sparkSession = SparkSession.builder().sparkContext(sparkContext.sc()).getOrCreate();
        Dataset<Row> dataset = sparkSession.createDataFrame(javaRDD, MlLibWarn.class);
        dataset = dataset.sort("random");
        // TODO 全部用于训练, 而非二八分（两份测试, 八份训练）
        dataset.randomSplit(new double[]{0.8, 0.2});


        VectorAssembler assembler = new VectorAssembler().setInputCols(new String[]{"time"}).setOutputCol("features");
        Dataset<Row> transform = assembler.transform(dataset);
        Dataset<Row>[] datasets = transform.randomSplit(new double[]{0.8, 0.2});


        // FIXME 保序回归
        IsotonicRegression isotonicRegression = new IsotonicRegression().setFeaturesCol("features").setLabelCol("count");
        IsotonicRegressionModel isotonicRegressionModel = isotonicRegression.fit(datasets[0]);

        // 缓存模型
        warnModel = isotonicRegressionModel;

        // 2019/4/23 测试 11~20波动不等, 与实际值有一定差距
        isotonicRegressionModel.transform(datasets[1]).show();

        log.warn("=========================================");
        log.warn("=========================================");
        log.warn("=========================================");

        // FIXME 线性回归
//        LinearRegression linearRegression = new LinearRegression().setMaxIter(10).setRegParam(0.3).setElasticNetParam(0.8);
//        linearRegression.setFeaturesCol("features").setLabelCol("count");
//        LinearRegressionModel linearRegressionModel = linearRegression.fit(datasets[0]);
//        linearRegressionModel.transform(datasets[1]).show();   // 2019/4/23 测试 13+-1

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
//        logisticResult.show();      // 2019/4/23 测试稳定4.0

        return "finish...";

    }


    @GetMapping("/trainWifi")
    private String trainWifi() {

        Configuration configuration = hbaseTemplate.getConfiguration();
        configuration.set(TableInputFormat.INPUT_TABLE, Constants.WifiTable.TABLE_NAME);

        JavaPairRDD<ImmutableBytesWritable, Result> hbaseRDD = sparkContext.newAPIHadoopRDD(
                configuration,
                TableInputFormat.class,
                ImmutableBytesWritable.class,
                Result.class
        );
        ThreadLocalRandom random = ThreadLocalRandom.current();
        JavaRDD<MlLibWifi> javaRDD = hbaseRDD.map(immutableBytesWritableResultTuple2 -> {
            List<MlLibWifi> mlLibWifis = Lists.newArrayList();
            Result result = immutableBytesWritableResultTuple2._2();
            String mac = Bytes.toString(result.getValue(
               Constants.WifiTable.FAMILY_U.getBytes(),
               Constants.WifiTable.MAC.getBytes()
            ));
            mac = StringUtils.substring(mac, 0, mac.indexOf("-"));
            String mMac = Bytes.toString(result.getValue(
                    Constants.WifiTable.FAMILY_D.getBytes(),
                    Constants.WifiTable.MMAC.getBytes()
            ));
            MlLibWifi mlLibWifi = new MlLibWifi(mac, mMac, random.nextDouble());
            return mlLibWifi;
        });


        SparkSession sparkSession = SparkSession.builder().sparkContext(sparkContext.sc()).getOrCreate();
        Dataset<Row> dataset = sparkSession.createDataFrame(javaRDD, MlLibWarn.class);
//        dataset = dataset.groupBy("mac").sort("random");
        RelationalGroupedDataset groupDataSet = dataset.groupBy("mac");
        groupDataSet.count().show();
//        // TODO 全部用于训练, 而非二八分（两份测试, 八份训练）
//        dataset.randomSplit(new double[]{0.8, 0.2});
//
//
//        VectorAssembler assembler = new VectorAssembler().setInputCols(new String[]{"time"}).setOutputCol("features");
//        Dataset<Row> transform = assembler.transform(dataset);
//        Dataset<Row>[] datasets = transform.randomSplit(new double[]{0.8, 0.2});
//
//
//        // FIXME 保序回归
//        IsotonicRegression isotonicRegression = new IsotonicRegression().setFeaturesCol("features").setLabelCol("count");
//        IsotonicRegressionModel isotonicRegressionModel = isotonicRegression.fit(datasets[0]);
//
//        // 缓存模型
//        warnModel = isotonicRegressionModel;
//
//        // 2019/4/23 测试 11~20波动不等, 与实际值有一定差距
//        isotonicRegressionModel.transform(datasets[1]).show();

        return "SUCCESS";
    }
    /**
     * 该方法测试: 将训练好的结果模型缓存是否有效
     * 测试数据为: 随机制造的warn数据
     *
     * 可行, 但不确定是否合适
     * @return
     */
    @GetMapping("/prediction")
    private String prediction() {
        // TODO 已调通, 做定时任务5s执行?

        Instant now = Instant.now();
        long start = now.toEpochMilli();
        long end = now.plus(1, ChronoUnit.HOURS).toEpochMilli();
        List<MlLibWarn> mlLibWarnList = Lists.newArrayList();
        ThreadLocalRandom random = ThreadLocalRandom.current();
        for (long i = start; i <= end; i += 10 * 1000) {
            mlLibWarnList.add(new MlLibWarn(i, 0, random.nextDouble()));
        }
        SparkSession sparkSession = SparkSession.builder().sparkContext(sparkContext.sc()).getOrCreate();
        Dataset<Row> dataset = sparkSession.createDataFrame(mlLibWarnList, MlLibWarn.class).sort("random");
        VectorAssembler assembler = new VectorAssembler().setInputCols(new String[]{"time"}).setOutputCol("features");
        Dataset<Row> transform = assembler.transform(dataset);
        transform.show();
        transform = warnModel.transform(transform);
        transform.show();
//        StringJoiner joiner = new StringJoiner("\n");
//        transform.select("prediction").foreach(row -> {
//            joiner.add(row.get(0).toString());
//        });
//        return joiner.toString();
        return null;
    }




}
