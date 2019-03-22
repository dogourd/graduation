package top.ezttf.graduation.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.assertj.core.util.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import top.ezttf.graduation.vo.ReceiveData;

import java.util.Date;

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

    @Test
    public void testSaveOrUpdates() {

        log.debug("{}", new Date());

        ReceiveData receiveData = new ReceiveData("id", Lists.newArrayList(),
                "mmac", 2,
                DateUtils.addDays(new Date(), 1), "lat", "lon");
        iWarnService.saveWarn(receiveData);

        receiveData = new ReceiveData("idid", Lists.newArrayList(),
                "mmac", 2,
                DateUtils.addDays(new Date(), 2), "latlat", "lonlon");
        iWarnService.saveWarn(receiveData);
    }
}
