package top.ezttf.graduation.mapper;

import com.spring4all.spring.boot.starter.hbase.api.RowMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.http.client.utils.DateUtils;
import top.ezttf.graduation.constant.Constants;
import top.ezttf.graduation.vo.Wifi;

import java.util.Date;

/**
 * @author yuwen
 * @date 2019/3/20
 */
public class WifiRowMapper implements RowMapper<Wifi> {

    private static byte[] FAMILY_D = Constants.WifiTable.FAMILY_D.getBytes();
    private static byte[] ID = Constants.WifiTable.ID.getBytes();
    private static byte[] MMAC = Constants.WifiTable.MMAC.getBytes();
    private static byte[] FAMILY_U = Constants.WifiTable.FAMILY_U.getBytes();
    private static byte[] MAC = Constants.WifiTable.MAC.getBytes();
    private static byte[] RANGE = Constants.WifiTable.RANGE.getBytes();
    private static byte[] FAMILY_T = Constants.WifiTable.FAMILY_T.getBytes();
    private static byte[] TIME = Constants.WifiTable.TIME.getBytes();


    @Override
    public Wifi mapRow(Result result, int rowNum) throws Exception {
        Wifi wifi = new Wifi();
        wifi.setId(Bytes.toString(result.getValue(FAMILY_D, ID)));
        wifi.setMmac(Bytes.toString(result.getValue(FAMILY_D, MMAC)));
        wifi.setMac(Bytes.toString(result.getValue(FAMILY_U, MAC)));
        wifi.setRange(Bytes.toDouble(result.getValue(FAMILY_U, RANGE)));

        String[] patterns = new String[]{"yyyy-MM-dd HH:mm:ss"};
        String timeStr = Bytes.toString(result.getValue(FAMILY_T, TIME));
        if (StringUtils.isNotBlank(timeStr)) {
            wifi.setTime(DateUtils.parseDate(timeStr, patterns));
        } else {
            wifi.setTime(new Date());
        }
        wifi.setRowKey(Bytes.toString(result.getRow()));
        return wifi;
    }
}
