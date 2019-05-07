package top.ezttf.graduation.controller.spark;

import com.google.common.collect.Lists;
import com.spring4all.spring.boot.starter.hbase.api.HbaseTemplate;
import lombok.extern.slf4j.Slf4j;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.ml.regression.IsotonicRegressionModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import top.ezttf.graduation.dao.DeviceRepository;
import top.ezttf.graduation.pojo.Device;
import top.ezttf.graduation.service.ISparkService;

import java.util.List;
import java.util.Optional;

/**
 * @author yuwen
 * @date 2019/3/18
 */
@Slf4j
@RestController
public class SparkController {


    private static IsotonicRegressionModel warnModel;
    private static IsotonicRegressionModel wifiModel;



    private final HbaseTemplate hbaseTemplate;
    private final JavaSparkContext sparkContext;
    private final ISparkService iSparkService;
    private final DeviceRepository deviceRepository;


    public SparkController(
            HbaseTemplate hbaseTemplate,
            JavaSparkContext sparkContext,
            ISparkService iSparkService,
            DeviceRepository deviceRepository) {
        this.hbaseTemplate = hbaseTemplate;
        this.sparkContext = sparkContext;
        this.iSparkService = iSparkService;
        this.deviceRepository = deviceRepository;
    }


    /**
     * 测试spark mllib回归
     * 包含: 保序回归、线性回归、逻辑回归
     * <p>
     * 该方法训练的数据来源于hbase数据表'graduation:warn'
     *
     * @return
     */
    @GetMapping("/trainWarn")
    public String trainWarn() {
        iSparkService.trainWarn();
        return "Success";
    }


    @GetMapping("/trainWifi")
    private String trainWifi() {
        iSparkService.trainWifi();
        return "Success";
    }

    /**
     * 该方法测试: 将训练好的结果模型缓存是否有效
     * 测试数据为: 随机制造的warn数据
     * <p>
     * 可行, 但不确定是否合适
     *
     * @return
     */
    @GetMapping("/predicateWarn")
    private String prediction() {
        iSparkService.predicateWarn().show();
        return "Success";
//        // TODO 已调通, 做定时任务5s执行?
    }

    @GetMapping("/predicateWifi")
    private List<Device> predicateWifi(double lastGeo) {
        List<Long> longs = iSparkService.predicateWifi(lastGeo);
        List<Device> list = Lists.newArrayList();
        longs.forEach(id -> {
            Optional<Device> optional = deviceRepository.findById(id);
            optional.ifPresent(list::add);
        });
        return list;
    }


}
