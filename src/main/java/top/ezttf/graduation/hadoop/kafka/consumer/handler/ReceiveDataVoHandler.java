package top.ezttf.graduation.hadoop.kafka.consumer.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import top.ezttf.graduation.dao.mysql.RowDataRepository;
import top.ezttf.graduation.entity.MySqlRowData;
import top.ezttf.graduation.vo.ReceiveDataVo;

import java.util.Date;

/**
 * @author yuwen
 * @date 2019/3/6
 */
@Component
public class ReceiveDataVoHandler {

    private final RowDataRepository rowDataRepository;

    @Autowired
    public ReceiveDataVoHandler(RowDataRepository rowDataRepository) {
        this.rowDataRepository = rowDataRepository;
    }

    public void handleReceiveDataVo(ReceiveDataVo vo) {
        // 探针设备id
        String deviceId = vo.getId();
        // 探针设备mac 地址
        String mmac = vo.getMmac();
        // 该数据收集时间
        Date startTime = vo.getTime();
        //startTime = DateUtils.addHours(startTime, 8);

        // 获取range: 手机距离嗅探设备的距离
        // 获取mac: 手机的mac地址
        vo.getUserInfos().forEach(userInfo -> {
            MySqlRowData mySqlRowData = new MySqlRowData(
                    deviceId, mmac, startTime, userInfo.getRange(), userInfo.getMac()
            );
            rowDataRepository.save(mySqlRowData);
        });

    }
}
