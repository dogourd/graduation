package top.ezttf.graduation.service;

import com.spring4all.spring.boot.starter.hbase.api.HbaseTemplate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.spark.SparkConf;
import org.apache.spark.streaming.Duration;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.assertj.core.util.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import top.ezttf.graduation.constant.Constants;
import top.ezttf.graduation.mapper.WarnRowMapper;
import top.ezttf.graduation.vo.ReceiveData;
import top.ezttf.graduation.vo.UserInfo;
import top.ezttf.graduation.vo.Warn;
import top.ezttf.graduation.vo.Wifi;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author yuwen
 * @date 2019/3/21
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class WarnServiceTest {

    @Autowired
    private IWarnService iWarnService;

    @Autowired
    private IWifiService iWifiService;

    @Autowired
    private HbaseTemplate hbaseTemplate;

    private static String[] deviceIds;

    static {
        // 模拟 10 个探针
        deviceIds = new String[10];
        for (int i = 0; i < deviceIds.length; i++) {
            // 生成随机 设备id
            deviceIds[i] = UUID.randomUUID().toString();
        }
    }

    @Test
    public void testSaveOrUpdates() {

        log.debug("{}", new Date());
        // 录 1w 条数据
        for (int i = 0; i < 10000; i++) {
            // 随机生成人流量大小 40以内
            int listSize = ThreadLocalRandom.current().nextInt(0, 40);
            List<UserInfo> list = Lists.newArrayList();
            for (int j = 0; j < listSize; j++) {
                // 生成随机信道值 20以内
                int element = ThreadLocalRandom.current().nextInt(20);
                UserInfo userInfo = new UserInfo();
                userInfo.setCh(element);
                list.add(userInfo);
                int deviceIdIndex = ThreadLocalRandom.current().nextInt(0, 10);
                // 每次将收集时间推迟 9 秒
                //
                ReceiveData receiveData = new ReceiveData(deviceIds[deviceIdIndex], list,
                        deviceIds[deviceIdIndex], 2,
                        DateUtils.addSeconds(new Date(), 9), "lat", "lon");
                Warn warn = Warn.assembleFromReceiveData(receiveData);
                List<Wifi> wifiList = Wifi.createWifisFromData(receiveData);
                iWarnService.saveWarn(warn);
                iWifiService.saveWifis(wifiList);
            }
        }

//        ReceiveData receiveData = new ReceiveData("id", Lists.newArrayList(),
//                "mmac", 2,
//                DateUtils.addDays(new Date(), 1), "lat", "lon");
//        Warn warn = Warn.assembleFromReceiveData(receiveData);
//        iWarnService.saveWarn(warn);
//
//        receiveData = new ReceiveData("idid", Lists.newArrayList(),
//                "mmac", 2,
//                DateUtils.addDays(new Date(), 2), "latlat", "lonlon");
//        iWarnService.saveWarn(Warn.assembleFromReceiveData(receiveData));
//
//        Scan scan = new Scan();
//        List<Warn> warnList = hbaseTemplate.find(Constants.WarnTable.TABLE_NAME, scan, new WarnRowMapper());
//        warnList.forEach(warnE -> log.debug(JSON.toJSONString(warnE)));
    }

    public void testSpark() throws InterruptedException {
        SparkConf sparkConf = new SparkConf().setAppName("computeHbase").setMaster("local[2]");
        JavaStreamingContext streamingContext = new JavaStreamingContext(sparkConf, new Duration(30 * 1000));
        List<Warn> warnList = hbaseTemplate.find(Constants.WarnTable.TABLE_NAME, new Scan(), new WarnRowMapper());

        streamingContext.start();
        streamingContext.awaitTermination();
    }

    @Test
    public void readHbase() {
        List<Warn> warns = hbaseTemplate.find("graduation:warn", new Scan(), new WarnRowMapper());
        warns.forEach(warn -> log.debug("{}", warn.getCount()));
    }

    @Test
    public void findAll() {
        List<Warn> warns = hbaseTemplate.find("graduation:warn", Constants.WarnTable.FAMILY_I, new WarnRowMapper());
        warns.forEach(warn -> log.info(warn.getCount().toString()));
    }
}
