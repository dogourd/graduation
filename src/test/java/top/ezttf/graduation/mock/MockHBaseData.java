package top.ezttf.graduation.mock;

import com.google.common.collect.Sets;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import top.ezttf.graduation.dao.DeviceRepository;
import top.ezttf.graduation.pojo.Device;
import top.ezttf.graduation.service.IWarnService;
import top.ezttf.graduation.service.IWifiService;
import top.ezttf.graduation.vo.ReceiveData;
import top.ezttf.graduation.vo.UserInfo;
import top.ezttf.graduation.vo.Warn;
import top.ezttf.graduation.vo.Wifi;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author yuwen
 * @date 2019/6/10
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class MockHBaseData {

    private ThreadPoolExecutor pool = new ThreadPoolExecutor(
            150,
            150,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(20000),
            new ThreadFactoryBuilder().setNameFormat("thread-pool-%d").build(),
            new ThreadPoolExecutor.AbortPolicy()
    );

    @Autowired
    private IWarnService iWarnService;
    @Autowired
    private IWifiService iWifiService;
    @Autowired
    private DeviceRepository repository;

    private String[] deviceIds;
    private String[] userMacs;
    private String[] geos;


    @Before
    public void setup() {
        deviceIds = new String[10];
        for (int i = 0; i < deviceIds.length; i++) {
            // 生成随机 设备id
            deviceIds[i] = UUID.randomUUID().toString();
        }
        Set<String> set = Sets.newHashSet(deviceIds);
        log.info("随机生成10个探针设备: {}", (Object) deviceIds);
        repository.deleteAll();
        geos = new String[]{"店A", "店B", "店C", "店D", "店E", "店F", "店G", "店H", "店I", "店J"};
        for (int i = 0; i < deviceIds.length; i++) {
            repository.save(new Device(null, deviceIds[i], geos[i], new Date(), new Date()));
        }

        userMacs = new String[1000];
        for (int i = 0; i < userMacs.length; i++) {
            String userMac = UUID.randomUUID().toString();
            while (set.contains(userMac)) {
                userMac = UUID.randomUUID().toString();
            }
            userMacs[i] = UUID.randomUUID().toString();
        }
        log.info("随机生成 1000 个用户 mac 地址");


    }


    /**
     * mock 1w 数据
     * 用户 mac 地址在 1000 个 uuid 中
     * 设备 mac 地址在 10 个 uuid 中
     * 每次收集数据的人流量大小在 400 以内
     * 用户和探针设备距离在 10.0 ~ 200.0 之间 (分米)
     *
     * 每一条数据 距离上一条推迟 1 豪秒
     */
    @Test
    public void testSaveOrUpdates() throws InterruptedException {

        // 录 1w 条数据
        List<UserInfo> list = Lists.newArrayList();
        for (int i = 0; i < 10000; i++) {
            // 随机生成人流量大小 400 以内
            int listSize = ThreadLocalRandom.current().nextInt(0, 400);
            // 生成随机人流量
            for (int j = 0; j < listSize; j++) {
                int userMacIndex = ThreadLocalRandom.current().nextInt(0, userMacs.length);
                UserInfo userInfo = new UserInfo();
                userInfo.setMac(userMacs[userMacIndex]);
                userInfo.setRange(ThreadLocalRandom.current().nextDouble(1.0, 20.0));
//                log.debug("当前生成用户: {}, 与探针距离: {}", userInfo.getMac(), userInfo.getRange());
                list.add(userInfo);
            }
            int deviceIdIndex = ThreadLocalRandom.current().nextInt(0, 10);
            // 每次将收集时间推迟 9 秒
            ReceiveData receiveData = new ReceiveData(deviceIds[deviceIdIndex], list,
                    deviceIds[deviceIdIndex], 2,
                    DateUtils.addMilliseconds(new Date(), 1), null, null);
//            log.info("收集设备: {}, 人流量大小: {}, 收集时间: {}",
//                    receiveData.getMmac(),
//                    receiveData.getUserInfos().size(),
//                    receiveData.getTime()
//            );
            Warn warn = Warn.assembleFromReceiveData(receiveData);
            List<Wifi> wifiList = Wifi.createWifisFromData(receiveData);
            pool.execute(() -> iWarnService.saveWarn(warn));
            pool.execute(() -> iWifiService.saveWifis(wifiList));
            list.clear();
        }
        pool.shutdown();
        while (!pool.isTerminated()) {
            Thread.sleep(10000);
        }
    }
}
