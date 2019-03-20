1. **HDFS**   
`$HADOOP_HOME/sbin/start-dfs.sh`
2. **Zookeeper**  
`$ZK_HOME/bin/zkServer start`
3. **Kafka**  
`nohup $KAFKA_HOME/bin/kafka-server-start.sh $KAFKA_HOME/config/server.properties &`
4. **Flume**  
`nohup $FLUME_HOME/bin/flume-ng agent \
--name kafka  \
--conf $FLUME_HOME/conf \
--conf-file $FLUME_HOME/conf/avro-memory-kafka.properties \
--no_reload_conf \
-Dflume.root.logger=INFO,console &`
5. **Spark**  
`$SPARK_HOME/sbin/start-all.sh`
6. **HBase**  
`$HBASE_HOME/bin/start-hbase.sh`