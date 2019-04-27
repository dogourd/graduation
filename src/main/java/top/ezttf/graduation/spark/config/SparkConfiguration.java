package top.ezttf.graduation.spark.config;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author yuwen
 * @date 2019/4/27
 */
@Configuration
public class SparkConfiguration {

    @Bean
    public SparkConf sparkConf() {
        return new SparkConf()
                .setMaster("local[2]")
                .setAppName("grad")
                .set("spark.executor.memory", "512m");
    }

    @Bean
    public JavaSparkContext javaSparkContext() {
        JavaSparkContext sparkContext = new JavaSparkContext(sparkConf());
        sparkContext.setLogLevel("WARN");
        return sparkContext;
    }
}
