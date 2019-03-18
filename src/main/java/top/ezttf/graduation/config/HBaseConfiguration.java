package top.ezttf.graduation.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.hadoop.hbase.HbaseTemplate;

/**
 * @author yuwen
 * @date 2019/3/7
 */
@Configuration
public class HBaseConfiguration {

    @Value("${project.hbase.zookeeper.quorum}")
    private String zookeeperQuorum;

    @Value(("${project.hbase.zookeeper.property.port}"))
    private String port;

    @Value("${project.zookeeper.znode.parent}")
    private String zoneParent;

    @Bean
    public HbaseTemplate hbaseTemplate() {
        org.apache.hadoop.conf.Configuration configuration = new org.apache.hadoop.conf.Configuration();
        configuration.set("hbase.zookeeper.quorum", zookeeperQuorum);
        configuration.set("hbase.zookeeper.property.clientPort", port);
        configuration.set("zookeeper.znode.parent", zoneParent);
        return new HbaseTemplate(configuration);
    }
}
