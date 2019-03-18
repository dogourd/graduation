package top.ezttf.graduation.dao.hbase;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.hadoop.hbase.HbaseTemplate;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.List;

/**
 * @author yuwen
 * @date 2019/3/7
 */
@Slf4j
@Repository
public class HBaseDao {

    private final HbaseTemplate hbaseTemplate;

    @Autowired
    public HBaseDao(HbaseTemplate hbaseTemplate) {
        this.hbaseTemplate = hbaseTemplate;
    }

    public void createTable(String tableName, List<String> columnFamily) {
        if (StringUtils.isBlank(tableName) || CollectionUtils.isEmpty(columnFamily)) {
            log.error("表名或列族为空无法创建hbase数据表");
            return;
        }
        Configuration configuration = hbaseTemplate.getConfiguration();
        try (Connection connection = ConnectionFactory.createConnection(configuration);
             Admin admin = connection.getAdmin()
        ) {
            if (!admin.isTableAvailable(TableName.valueOf(tableName))) {
                HTableDescriptor table = new HTableDescriptor(TableName.valueOf(tableName));
                columnFamily.forEach(column -> table.addFamily(new HColumnDescriptor(column)));
                admin.createTable(table);
            }
        } catch (IOException e) {
            log.error("create hbase table error", e);
        }
    }


    private void put(Object data, String tableName, String column, String rowKey) {
        if (data == null || StringUtils.isBlank(tableName) || StringUtils.isBlank(column)) {
            log.warn("录入的数据可能为空，或者表名，列名为空。忽略该操作");
            return;
        }
        //hbaseTemplate.execute(tableName, table -> {
        //    PropertyDescriptor[] descriptors = BeanUtils.getPropertyDescriptors(data.getClass());
        //    BeanWrapper beanWrapper = PropertyAccessorFactory.forBeanPropertyAccess(data);
        //    Put put = new Put(Bytes.toBytes(rowKey));
        //    for (PropertyDescriptor descriptor : descriptors) {
        //        String propertyName = descriptor.getName();
        //        String value = Objects.requireNonNull(beanWrapper.getPropertyValue(propertyName)).toString();
        //        if (StringUtils.isNotBlank(value)) {
        //            // todo 对应一下参数列表
        //            put.add(CellUtil.createCell(
        //                    Bytes.toBytes(column), Bytes.toBytes(propertyName), Bytes.toBytes(value))
        //            );
        //        }
        //    }
        //});
    }
}
