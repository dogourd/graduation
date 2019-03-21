package top.ezttf.graduation.mapper;

import com.spring4all.spring.boot.starter.hbase.api.RowMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.http.client.utils.DateUtils;
import top.ezttf.graduation.constant.Constants;
import top.ezttf.graduation.vo.Warn;

import java.util.Date;

/**
 * @author yuwen
 * @date 2019/3/21
 */
public class WarnRowMapper implements RowMapper<Warn> {

    private static byte[] FAMILY_D = Constants.WarnTable.FAMILY_D.getBytes();
    private static byte[] ID = Constants.WarnTable.ID.getBytes();
    private static byte[] MMAC = Constants.WarnTable.MMAC.getBytes();
    private static byte[] FAMILY_I = Constants.WarnTable.FAMILY_I.getBytes();
    private static byte[] COUNT = Constants.WarnTable.COUNT.getBytes();
    private static byte[] FAMILY_T = Constants.WarnTable.FAMILY_T.getBytes();
    private static byte[] TIME = Constants.WarnTable.TIME.getBytes();


    @Override
    public Warn mapRow(Result result, int rowNum) throws Exception {
        Warn warn = new Warn();
        warn.setId(Bytes.toString(result.getValue(FAMILY_D, ID)));
        warn.setMmac(Bytes.toString(result.getValue(FAMILY_D, MMAC)));
        warn.setCount(Bytes.toLong(result.getValue(FAMILY_I, COUNT)));

        String[] patterns = new String[]{"yyyy-MM-dd HH:mm:ss"};
        String timeStr = Bytes.toString(result.getValue(FAMILY_T, TIME));
        if (StringUtils.isNotBlank(timeStr)) {
            warn.setTime(DateUtils.parseDate(timeStr, patterns));
        } else {
            warn.setTime(new Date());
        }
        return warn;
    }
}
