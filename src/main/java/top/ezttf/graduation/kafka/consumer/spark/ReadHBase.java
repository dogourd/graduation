package top.ezttf.graduation.kafka.consumer.spark;

import com.spring4all.spring.boot.starter.hbase.api.HbaseTemplate;
import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableInputFormat;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.rdd.RDD;
import org.springframework.stereotype.Component;
import scala.Tuple2;
import scala.runtime.AbstractFunction1;
import scala.runtime.BoxedUnit;
import top.ezttf.graduation.constant.Constants;

/**
 * @author yuwen
 * @date 2019/4/11
 */
@Slf4j
@Component
public class ReadHBase {

    private final HbaseTemplate hbaseTemplate;

    public ReadHBase(HbaseTemplate hbaseTemplate) {
        this.hbaseTemplate = hbaseTemplate;
    }

    public void readHbase() {
        SparkConf sparkConf = new SparkConf().setAppName("readHbase").setMaster("local[2]");
        SparkContext sparkContext = new SparkContext(sparkConf);
        Configuration configuration = hbaseTemplate.getConfiguration();
        configuration.set(TableInputFormat.INPUT_TABLE, Constants.WarnTable.TABLE_NAME);
        RDD<Tuple2<ImmutableBytesWritable, Result>> hbaseRDD =
                sparkContext.newAPIHadoopRDD(
                        hbaseTemplate.getConfiguration(),
                        TableInputFormat.class,
                        ImmutableBytesWritable.class,
                        Result.class
                );
        long count = hbaseRDD.count();
        log.info("count: {}", count);
        hbaseRDD.foreach(new AbstractFunction1<Tuple2<ImmutableBytesWritable, Result>, BoxedUnit>() {
            @Override
            public BoxedUnit apply(Tuple2<ImmutableBytesWritable, Result> immutableBytesWritableResultTuple2) {
                Result result = immutableBytesWritableResultTuple2._2();
                // 行键
                String key = Bytes.toString(result.getRow());
                String value = Bytes.toString(result.getValue(
                        Constants.WarnTable.FAMILY_I.getBytes(),
                        Constants.WarnTable.COUNT.getBytes())
                );
                log.debug(key + "   " + value);
                return null;
            }
        });
        sparkContext.stop();
    }
}
