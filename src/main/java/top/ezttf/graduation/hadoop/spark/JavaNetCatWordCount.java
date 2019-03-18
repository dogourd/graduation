package top.ezttf.graduation.hadoop.spark;

import org.apache.spark.SparkConf;
import org.apache.spark.storage.StorageLevel;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaReceiverInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import scala.Tuple2;

import java.util.Arrays;

/**
 * Spark Streaming 处理socket数据
 *
 * @author yuwen
 * @date 2018/12/2
 */
public class JavaNetCatWordCount {

    public static void execute() throws InterruptedException {
        SparkConf sparkConf = new SparkConf()
                .setMaster("local[2]")
                .setAppName("NetCat wordCount");
        // 初始化 StreamingContext, 配置sparkConf 和 batch interval
        JavaStreamingContext streamingContext = new JavaStreamingContext(sparkConf, Durations.seconds(5));
        JavaReceiverInputDStream<String> lines = streamingContext.socketTextStream(
                "hadoop", 30031, StorageLevel.MEMORY_AND_DISK_SER_2()
        );
        JavaDStream<String> words = lines.flatMap(s -> Arrays.asList(s.split(" ")).iterator());
        JavaPairDStream<String, Integer> wordCounts = words.mapToPair(s -> new Tuple2<>(s, 1))
                .reduceByKey((v1, v2) -> v1 + v2);
        wordCounts.print();
        streamingContext.start();
        streamingContext.awaitTermination();
    }

    public static void main(String[] args) throws InterruptedException {
        execute();
    }

}
