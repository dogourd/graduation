package top.ezttf.graduation.noweb;

import lombok.extern.slf4j.Slf4j;
import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.sql.SparkSession;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import scala.Tuple2;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;

/**
 * @author yuwen
 * @date 2019/3/10
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class SparkMySqlTest {

    @Autowired
    private DataSource dataSource;

    private static final Pattern SPACE = Pattern.compile(" ");

    @Test
    public void testSparkReadMySql() throws SQLException, InterruptedException {

        SparkConf sparkConf = new SparkConf()
                .setMaster("spark://hadoop:7077")
                .setAppName("sparkReadMySql")
                .set("spark.cores.max", "1")
                .set("spark.executor.memory", "1g")
                .set("deploy.mode", "cluster");
        JavaSparkContext context = new JavaSparkContext(sparkConf);
        //JavaStreamingContext ssc = new JavaStreamingContext(sparkConf, Durations.seconds(1));

        String jdbcUrl = "jdbc:mysql:///grad?characterEncoding=utf-8&useSSL=false";
        String table = "tb_row_data";
        Properties properties = new Properties();
        properties.put("user", "yuwen");
        properties.put("password", "lyp82nlf");
        properties.put("driver", "com.mysql.jdbc.Driver");
        Connection connection = dataSource.getConnection();
        SQLContext sqlContext = new SQLContext(context);
        SparkSession sparkSession = SparkSession.builder().sparkContext(context.sc()).getOrCreate();
        Dataset<Row> dataset = sparkSession.sqlContext()
                .read()
                .jdbc(jdbcUrl, table, properties)
                .select("mmac")
                .limit(10);
        dataset.foreach(data -> {
            log.debug(data.toString());
        });
        dataset.show();
        context.stop();
    }

    @Test
    public void testWordCount() {
        SparkConf sparkConf = new SparkConf().setMaster("spark://hadoop:7077")
                .setAppName("wordCount")
                .setJars(new String[]{"target\\graduation-1.9.jar"});
        SparkSession spark = SparkSession.builder().master("spark://hadoop:7077")
                .appName("wordCount")
                .getOrCreate();
        JavaRDD<String> lines = spark.read().textFile("hdfs://hadoop:8020/README.txt").javaRDD();
        JavaRDD<String> words = lines.flatMap(word ->
                Arrays.asList(Pattern.compile(" ").split(word)).iterator());
        JavaPairRDD<String, Integer> ones = words.mapToPair(word -> new Tuple2<>(word, 1));
        JavaPairRDD<String, Integer> counts = ones.reduceByKey((i, j) -> i + j);
        List<Tuple2<String, Integer>> output = counts.collect();
        output.forEach(out -> System.out.println(out._1() + ": " + out._2()));
        spark.stop();
    }

    @Test
    public void testWordCount2() {
        SparkConf sparkConf = new SparkConf().setMaster("spark://hadoop:7077")
                .setAppName("wordCount")
                .set("spark.executor.memory", "512m")
                .setJars(new String[]{"target\\graduation-1.0.jar"});
        SparkContext context = new SparkContext(sparkConf);
        SparkSession spark = new SparkSession(context);
        JavaRDD<String> lines = spark.read().textFile("hdfs://hadoop:8020/README.txt").javaRDD();
        JavaRDD<String> words = lines.flatMap(word ->
                Arrays.asList(Pattern.compile(" ").split(word)).iterator());
        JavaPairRDD<String, Integer> ones = words.mapToPair(word -> new Tuple2<>(word, 1));
        JavaPairRDD<String, Integer> counts = ones.reduceByKey((i, j) -> i + j);
        List<Tuple2<String, Integer>> output = counts.collect();
        output.forEach(out -> System.out.println(out._1() + ": " + out._2()));
        spark.stop();
    }
}
