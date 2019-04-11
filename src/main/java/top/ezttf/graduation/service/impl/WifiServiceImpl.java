package top.ezttf.graduation.service.impl;

import com.google.common.collect.Lists;
import com.spring4all.spring.boot.starter.hbase.api.HbaseTemplate;
import org.apache.hadoop.hbase.client.Mutation;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.http.client.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.ezttf.graduation.constant.Constants;
import top.ezttf.graduation.service.IWifiService;
import top.ezttf.graduation.utils.RowKeyGenUtil;
import top.ezttf.graduation.vo.Wifi;

import java.util.List;

/**
 * @author yuwen
 * @date 2019/3/21
 */
@Service
public class WifiServiceImpl implements IWifiService {

    private final HbaseTemplate hbaseTemplate;

    public WifiServiceImpl(HbaseTemplate hbaseTemplate) {
        this.hbaseTemplate = hbaseTemplate;
    }

    @Override
    public void saveWifis(List<Wifi> wifis) {
        List<Mutation> datas = Lists.newArrayList();
        wifis.forEach(wifi -> {
            String mmac = RowKeyGenUtil.genWifiRowKey(wifi);
            byte[] FAMILY_D = Constants.WifiTable.FAMILY_D.getBytes();
            byte[] ID = Constants.WifiTable.ID.getBytes();
            byte[] MMAC = Constants.WifiTable.MMAC.getBytes();
            byte[] FAMILY_U = Constants.WifiTable.FAMILY_U.getBytes();
            byte[] MAC = Constants.WifiTable.MAC.getBytes();
            byte[] RANGE = Constants.WifiTable.RANGE.getBytes();
            byte[] FAMILY_T = Constants.WifiTable.FAMILY_T.getBytes();
            byte[] TIME = Constants.WifiTable.TIME.getBytes();

            Put put = new Put(Bytes.toBytes(mmac));
            put.addColumn(FAMILY_D, ID, Bytes.toBytes(wifi.getId()));
            put.addColumn(FAMILY_D, MMAC, Bytes.toBytes(wifi.getMmac()));
            put.addColumn(FAMILY_U, MAC, Bytes.toBytes(wifi.getMac()));
            put.addColumn(FAMILY_U, RANGE, Bytes.toBytes(wifi.getRange()));
            String pattern = "yyyy-MM-dd HH:mm:ss";
            put.addColumn(FAMILY_T, TIME, Bytes.toBytes(DateUtils.formatDate(wifi.getTime(), pattern)));
            datas.add(put);
        });
        hbaseTemplate.saveOrUpdates(Constants.WifiTable.TABLE_NAME, datas);
    }
}
