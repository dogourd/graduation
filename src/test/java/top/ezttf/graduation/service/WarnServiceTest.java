package top.ezttf.graduation.service;

import com.alibaba.fastjson.JSON;
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
import top.ezttf.graduation.vo.Warn;

import java.util.Date;
import java.util.List;

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
    private HbaseTemplate hbaseTemplate;

    @Test
    public void testSaveOrUpdates() {

        log.debug("{}", new Date());

        ReceiveData receiveData = new ReceiveData("id", Lists.newArrayList(),
                "mmac", 2,
                DateUtils.addDays(new Date(), 1), "lat", "lon");
        Warn warn = Warn.assembleFromReceiveData(receiveData);
        iWarnService.saveWarn(warn);

        receiveData = new ReceiveData("idid", Lists.newArrayList(),
                "mmac", 2,
                DateUtils.addDays(new Date(), 2), "latlat", "lonlon");
        iWarnService.saveWarn(Warn.assembleFromReceiveData(receiveData));

        Scan scan = new Scan();
        List<Warn> warnList = hbaseTemplate.find(Constants.WarnTable.TABLE_NAME, scan, new WarnRowMapper());
        warnList.forEach(warnE -> log.debug(JSON.toJSONString(warnE)));
    }

    public void testSpark() throws InterruptedException {
        SparkConf sparkConf = new SparkConf().setAppName("computeHbase").setMaster("local[2]");
        JavaStreamingContext streamingContext = new JavaStreamingContext(sparkConf, new Duration(30 * 1000));
        List<Warn> warnList = hbaseTemplate.find(Constants.WarnTable.TABLE_NAME, new Scan(), new WarnRowMapper());

        streamingContext.start();
        streamingContext.awaitTermination();
    }
}
