package top.ezttf.graduation.index;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import top.ezttf.graduation.dao.DeviceRepository;
import top.ezttf.graduation.pojo.Device;

import java.util.List;

/**
 * 加载全量索引
 *
 * @author yuwen
 * @date 2019/5/7
 */
@Slf4j
@Component
public class IndexInitLoader implements CommandLineRunner {

    private final DeviceRepository deviceRepository;

    public IndexInitLoader(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }


    @Override
    public void run(String... args) throws Exception {
        DeviceIndex deviceIndex = DataTable.of(DeviceIndex.class);
        List<Device> devices = deviceRepository.findAll();
        devices.forEach(device -> {
            deviceIndex.add(device.getMMac(), device.getId());
        });
    }
}
