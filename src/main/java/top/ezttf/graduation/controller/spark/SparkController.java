package top.ezttf.graduation.controller.spark;

import com.alibaba.fastjson.JSON;
import com.spring4all.spring.boot.starter.hbase.api.HbaseTemplate;
import lombok.extern.slf4j.Slf4j;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.ml.regression.IsotonicRegressionModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import top.ezttf.graduation.service.ISparkService;

import java.util.List;

/**
 * @author yuwen
 * @date 2019/3/18
 */
@Slf4j
@RestController
public class SparkController {


    private static IsotonicRegressionModel warnModel;
    private static IsotonicRegressionModel wifiModel;



    private final HbaseTemplate hbaseTemplate;
    private final JavaSparkContext sparkContext;
    private final ISparkService iSparkService;


    public SparkController(
            HbaseTemplate hbaseTemplate,
            JavaSparkContext sparkContext,
            ISparkService iSparkService) {
        this.hbaseTemplate = hbaseTemplate;
        this.sparkContext = sparkContext;
        this.iSparkService = iSparkService;
    }


    /**
     * 测试spark mllib回归
     * 包含: 保序回归、线性回归、逻辑回归
     * <p>
     * 该方法训练的数据来源于hbase数据表'graduation:warn'
     *
     * @return
     */
    @GetMapping("/trainWarn")
    public String trainWarn() {

        iSparkService.trainWarn();
        return "Success";


//        Configuration configuration = hbaseTemplate.getConfiguration();
//        configuration.set(TableInputFormat.INPUT_TABLE, Constants.WarnTable.TABLE_NAME);
//
//
//        JavaPairRDD<ImmutableBytesWritable, Result> hbaseRDD = sparkContext.newAPIHadoopRDD(
//                configuration,
//                TableInputFormat.class,
//                ImmutableBytesWritable.class,
//                Result.class
//        );
//        ThreadLocalRandom random = ThreadLocalRandom.current();
//        JavaRDD<MlLibWarn> javaRDD = hbaseRDD.map((Function<Tuple2<ImmutableBytesWritable, Result>, MlLibWarn>) v1 -> {
//            Result result = v1._2();
//            String t = Bytes.toString(result.getValue(
//                    Constants.WarnTable.FAMILY_T.getBytes(),
//                    Constants.WarnTable.TIME.getBytes()
//            ));
//            long time = Timestamp.valueOf(t).getTime();
//            long count = Bytes.toLong(result.getValue(
//                    Constants.WarnTable.FAMILY_I.getBytes(),
//                    Constants.WarnTable.COUNT.getBytes()
//            ));
//            return new MlLibWarn((double) time, (double) count, random.nextDouble());
//        }).distinct();
//
//
//        SparkSession sparkSession = SparkSession.builder().sparkContext(sparkContext.sc()).getOrCreate();
//        Dataset<Row> dataset = sparkSession.createDataFrame(javaRDD, MlLibWarn.class);
//        dataset = dataset.sort("random");
//        // TODO 全部用于训练, 而非二八分（两份测试, 八份训练）
//        dataset.randomSplit(new double[]{0.8, 0.2});
//
//
//        VectorAssembler assembler = new VectorAssembler().setInputCols(new String[]{"time"}).setOutputCol("features");
//        Dataset<Row> transform = assembler.transform(dataset);
//        Dataset<Row>[] datasets = transform.randomSplit(new double[]{0.8, 0.2});
//
//
//        // 保序回归
//        IsotonicRegression isotonicRegression = new IsotonicRegression().setFeaturesCol("features").setLabelCol("count");
//        IsotonicRegressionModel isotonicRegressionModel = isotonicRegression.fit(datasets[0]);
//
//        // 缓存模型
//        warnModel = isotonicRegressionModel;
//
//        // 2019/4/23 测试 11~20波动不等, 与实际值有一定差距
//        isotonicRegressionModel.transform(datasets[1]).show();
//
//        return "finish...";

    }


    @GetMapping("/trainWifi")
    private String trainWifi() {

        iSparkService.trainWifi();
        return "Success";

//        Configuration configuration = hbaseTemplate.getConfiguration();
//        configuration.set(TableInputFormat.INPUT_TABLE, Constants.WifiTable.TABLE_NAME);
//
//        JavaPairRDD<ImmutableBytesWritable, Result> hbaseRDD = sparkContext.newAPIHadoopRDD(
//                configuration,
//                TableInputFormat.class,
//                ImmutableBytesWritable.class,
//                Result.class
//        );
//        ThreadLocalRandom random = ThreadLocalRandom.current();
//        Map<String, List<String>> map = Maps.newHashMap();
//        JavaRDD<Map<String, List<String>>> javaRDD = hbaseRDD.map(immutableBytesWritableResultTuple2 -> {
//            Result result = immutableBytesWritableResultTuple2._2();
//            String mac = Bytes.toString(result.getValue(
//                    Constants.WifiTable.FAMILY_U.getBytes(),
//                    Constants.WifiTable.MAC.getBytes()
//            ));
//            mac = StringUtils.substring(mac, 0, mac.indexOf("-"));
//            String mMac = Bytes.toString(result.getValue(
//                    Constants.WifiTable.FAMILY_D.getBytes(),
//                    Constants.WifiTable.MMAC.getBytes()
//            ));
//            List<String> list = map.computeIfAbsent(mac, s -> new ArrayList<>());
//            list.add(mMac);
//            return map;
//        }).distinct();
//        List<Map<String, List<String>>> collect = javaRDD.collect();
//        Map<String, List<String>> finalMap = collect.get(0);
//
//        // 初始化只带表头的dataSet
//        SparkSession sparkSession = SparkSession.builder().sparkContext(sparkContext.sc()).getOrCreate();
//        Dataset<Row> dataset = sparkSession.createDataFrame(Lists.newArrayList(), MlLibWifi.class);
//
//        List<List<String>> mMacList = finalMap.values()
//                .stream()
//                .filter(mMacs -> mMacs.size() >= 2)
//                .collect(Collectors.toList());
//        for (List<String> mMacs : mMacList) {
//            String last = StringUtils.EMPTY;
//            for (Iterator<String> iterator = mMacs.iterator(); iterator.hasNext(); ) {
//                String next = iterator.next();
//                if (StringUtils.equals(next, last)) {
//                    iterator.remove();
//                } else {
//                    last = next;
//                }
//            }
//            List<MlLibWifi> mlLibWifis = Lists.newArrayList();
//            for (int i = 0; i < mMacs.size() - 1; i++) {
//                String lastGeo = mMacs.get(i);
//                String nowGeo = mMacs.get(i + 1);
//                double nextDouble = random.nextDouble();
//                MlLibWifi mlLibWifi = new MlLibWifi(
//                        Double.valueOf(StringUtils.substring(lastGeo, 4)),
//                        Double.valueOf(StringUtils.substring(nowGeo, 4)),
//                        nextDouble);
//                mlLibWifis.add(mlLibWifi);
//            }
//            Dataset<Row> dataFrame = sparkSession.createDataFrame(mlLibWifis, MlLibWifi.class);
//            System.out.println("dataFrame=================");
//            dataFrame.show();
//            dataset = dataset.union(dataFrame);
//            System.out.println("dataSet=================");
//            dataset.show();
//        }
//
//        dataset.show();
//        // FIXME 全部用于训练, 而非二八分（两份测试, 八份训练）
//        dataset.randomSplit(new double[]{0.8, 0.2});
//
//
//        // TODO 将训练集的自变量和数字对应
//        VectorAssembler assembler = new VectorAssembler().setInputCols(new String[]{"lastGeo"}).setOutputCol("features");
//        Dataset<Row> transform = assembler.transform(dataset);
//        Dataset<Row>[] datasets = transform.randomSplit(new double[]{0.8, 0.2});
//
//
//        // FIXME 保序回归
//        IsotonicRegression isotonicRegression = new IsotonicRegression().setFeaturesCol("features").setLabelCol("nowGeo");
//        IsotonicRegressionModel isotonicRegressionModel = isotonicRegression.fit(datasets[0]);
//
//
//        // TODO 已调通, 预测时应考虑对prediction结果进行四舍五入
//        // 缓存模型
//        wifiModel = isotonicRegressionModel;
//
//        // 2019/4/23 测试 11~20波动不等, 与实际值有一定差距
//        isotonicRegressionModel.transform(datasets[1]).show();
//
//        return JsonUtil.obj2StrPretty(collect);
    }

    /**
     * 该方法测试: 将训练好的结果模型缓存是否有效
     * 测试数据为: 随机制造的warn数据
     * <p>
     * 可行, 但不确定是否合适
     *
     * @return
     */
    @GetMapping("/predicateWarn")
    private String prediction() {
        iSparkService.predicateWarn().show();
        return "Success";
//        // TODO 已调通, 做定时任务5s执行?
//
//        Instant now = Instant.now();
//        long start = now.toEpochMilli();
//        long end = now.plus(1, ChronoUnit.HOURS).toEpochMilli();
//        List<MlLibWarn> mlLibWarnList = Lists.newArrayList();
//        ThreadLocalRandom random = ThreadLocalRandom.current();
//        for (long i = start; i <= end; i += 10 * 1000) {
//            mlLibWarnList.add(new MlLibWarn(i, 0, random.nextDouble()));
//        }
//        SparkSession sparkSession = SparkSession.builder().sparkContext(sparkContext.sc()).getOrCreate();
//        Dataset<Row> dataset = sparkSession.createDataFrame(mlLibWarnList, MlLibWarn.class).sort("random");
//        VectorAssembler assembler = new VectorAssembler().setInputCols(new String[]{"time"}).setOutputCol("features");
//        Dataset<Row> transform = assembler.transform(dataset);
//        transform.show();
//        transform = warnModel.transform(transform);
//        transform.show();
////        StringJoiner joiner = new StringJoiner("\n");
////        transform.select("prediction").foreach(row -> {
////            joiner.add(row.get(0).toString());
////        });
////        return joiner.toString();
//        return null;
    }

    @GetMapping("/predicateWifi")
    private String predicateWifi(double lastGeo) {
        List<Long> longs = iSparkService.predicateWifi(lastGeo);
        return JSON.toJSONString(longs);
    }


}
