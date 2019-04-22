package top.ezttf.graduation.controller.spark

import org.apache.hadoop.hbase.HBaseConfiguration
import org.apache.hadoop.hbase.client.Result
import org.apache.hadoop.hbase.io.ImmutableBytesWritable
import org.apache.hadoop.hbase.mapreduce.TableInputFormat
import org.apache.hadoop.hbase.util.Bytes
import org.apache.spark.ml.feature.VectorAssembler
import org.apache.spark.ml.regression.IsotonicRegression
import org.apache.spark.sql.SparkSession
import org.apache.spark.{SparkConf, SparkContext}

object ScalaHelper {

  def main() = {

    // 本地模式运行,便于测试
    val sparkConf = new SparkConf().setMaster("local").setAppName("HBaseTest")

    // 创建hbase configuration
    val hBaseConf = HBaseConfiguration.create()
    hBaseConf.set(TableInputFormat.INPUT_TABLE, "bmp_ali_customer")

    // 创建 spark context
    val sc = new SparkContext(sparkConf)
    val sqlContext = SparkSession.builder().config(sparkConf).getOrCreate()
    import sqlContext.implicits._

    // 从数据源获取数据
    val hbaseRDD = sc.newAPIHadoopRDD(
      hBaseConf,
      classOf[TableInputFormat],
      classOf[ImmutableBytesWritable],
      classOf[Result]
    )

    // 将数据映射为表  也就是将 RDD转化为 dataframe schema
    val data = hbaseRDD.map(r => (
      Bytes.toString(r._2.getValue(Bytes.toBytes("t"), Bytes.toBytes("time"))).toLong,
      Bytes.toString(r._2.getValue(Bytes.toBytes("i"), Bytes.toBytes("count"))).toLong
    )).toDF("time", "count")
    val assembler = new VectorAssembler().setInputCols(Array("time")).setOutputCol("time")
    val dataset = assembler.transform(data)
    val Array(train, test) = dataset.randomSplit(Array(0.8, 0.2))


    val isotonicRegression = new IsotonicRegression().setFeaturesCol("time").setLabelCol("count")
    val model = isotonicRegression.fit(train)
    model.transform(test).show()

  }


}
