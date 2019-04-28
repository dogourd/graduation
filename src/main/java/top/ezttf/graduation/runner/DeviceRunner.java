package top.ezttf.graduation.runner;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import top.ezttf.graduation.dao.DeviceRepository;
import top.ezttf.graduation.index.DeviceIndex;
import top.ezttf.graduation.pojo.Device;

import java.util.List;

/**
 * @author yuwen
 * @date 2019/4/27
 */
@Slf4j
@Component
public class DeviceRunner implements CommandLineRunner {


    private final DeviceIndex deviceIndex;
    private final DeviceRepository deviceRepository;

    public DeviceRunner(DeviceRepository deviceRepository, DeviceIndex deviceIndex) {
        this.deviceRepository = deviceRepository;
        this.deviceIndex = deviceIndex;
    }

    @Override
    public void run(String... args) throws Exception {
        List<Device> devices = deviceRepository.findAll();
        devices.forEach(device -> {
            deviceIndex.add(device.getMMac(), device.getId());
        });
    }
}
