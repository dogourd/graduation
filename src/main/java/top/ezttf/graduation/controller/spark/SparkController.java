package top.ezttf.graduation.controller.spark;

import com.alibaba.fastjson.JSON;
import com.spring4all.spring.boot.starter.hbase.api.HbaseTemplate;
import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableInputFormat;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.VoidFunction;
import org.apache.spark.rdd.RDD;
import org.apache.spark.sql.SparkSession;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import scala.Tuple2;
import top.ezttf.graduation.constant.Constants;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
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

    @GetMapping("/readHbase")
    public String readHbase() {
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
        long count = hbaseRDD.count();
        log.info("count: {}", count);
        StringBuilder builder = new StringBuilder();
        JavaRDD<Tuple2<ImmutableBytesWritable, Result>> javaRDD = hbaseRDD.toJavaRDD();
        javaRDD.foreach((VoidFunction<Tuple2<ImmutableBytesWritable, Result>>) immutableBytesWritableResultTuple2 -> {
            log.debug("666");
            Result result = immutableBytesWritableResultTuple2._2();
            // 行键
            String key = Bytes.toString(result.getRow());
            log.debug(key + "777");
            String value = Bytes.toString(result.getValue(
                    Constants.WarnTable.FAMILY_I.getBytes(),
                    Constants.WarnTable.COUNT.getBytes())
            );
            log.debug("key, value" + 888);
            log.debug(key + "   " + value);
            builder.append(key).append(": ").append(value).append("\n");
        });

        sparkContext.stop();
        log.debug(builder.toString());
        return JSON.toJSONString(builder.toString());
    }
}
