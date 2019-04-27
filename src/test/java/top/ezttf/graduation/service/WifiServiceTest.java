package top.ezttf.graduation.service;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import top.ezttf.graduation.vo.Wifi;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author yuwen
 * @date 2019/4/27
 */
@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class WifiServiceTest {

    @Autowired
    private IWifiService iWifiService;

    @Test
    public void testSaveOrUpdate() {
        List<String> macPool = Arrays.asList(
                "mac1", "mac2", "mac3",
                "mac4", "mac5", "mac6",
                "mac7", "mac8", "mac9", "mac10"
        );
        List<String> mMacPool = Arrays.asList(
                "mMac1", "mMac2", "mMac3",
                "mMac4", "mMac5", "mMac6",
                "mMac7", "mMac8", "mMac9", "mMac10"
        );
        ThreadLocalRandom random = ThreadLocalRandom.current();
        List<Wifi> wifis = Lists.newArrayList();
        for (int i = 0; i < 50; i++) {
            int index = random.nextInt(0, mMacPool.size());
            Wifi wifi = new Wifi(null, mMacPool.get(index),
                    mMacPool.get(index),
                    macPool.get(random.nextInt(0, macPool.size())),
                    10D, new Date()
            );
            wifis.add(wifi);
        }
        iWifiService.saveWifis(wifis);
    }
}
