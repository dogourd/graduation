package top.ezttf.graduation.spark;

import lombok.extern.slf4j.Slf4j;
import org.apache.spark.SparkConf;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;

import java.util.Arrays;

/**
 * 使用Spark Streaming处理文件系统(local / HDFS)的数据
 *
 * @author yuwen
 * @date 2018/12/8
 */
@Slf4j
public class JavaFileWordCount {

    public static void main(String[] args) throws InterruptedException {
        SparkConf sparkConf = new SparkConf().setMaster("local[2]").setAppName("fileWordCount");
        JavaStreamingContext streamingContext = new JavaStreamingContext(sparkConf, Durations.seconds(10L));

        JavaDStream<String> lines = streamingContext.textFileStream(args[0]);
        JavaDStream<String> dStream = lines
                .flatMap(s -> Arrays.asList(s.split(",")).iterator())
                .map(v1 -> v1);
        dStream.foreachRDD(objectJavaRDD -> log.debug(objectJavaRDD.toString()));

        streamingContext.start();
        streamingContext.awaitTermination();


        /**
         * 本机ip  192.168.0.110
         *
         * 虚拟机Ip 192.168.0.100
         *
         * 探针静态ip地址 192.168.0.120
         *
         *
         * lan 192.168.0.10
         * wan 192.168.0.120
         */
    }
}
