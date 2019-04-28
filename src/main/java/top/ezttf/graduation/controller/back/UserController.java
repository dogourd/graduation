package top.ezttf.graduation.controller.back;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import top.ezttf.graduation.dao.DeviceRepository;
import top.ezttf.graduation.index.DeviceIndex;
import top.ezttf.graduation.pojo.Device;

/**
 * @author yuwen
 * @date 2019/4/27
 */
@RestController("/device")
public class UserController {

    private final DeviceIndex deviceIndex;

    private final DeviceRepository deviceRepository;

    public UserController(DeviceRepository deviceRepository, DeviceIndex deviceIndex) {
        this.deviceRepository = deviceRepository;
        this.deviceIndex = deviceIndex;
    }

    @PostMapping("/add")
    public String addDevice(String mMac, String geo) {
        Device device = new Device(mMac, geo);
        device = deviceRepository.save(device);
        deviceIndex.add(device.getMMac(), device.getId());
        return "添加成功";
    }

    public String removeDevice(String mMac) {
        Device device = deviceRepository.findByMMac(mMac);
        if (device != null) {
            deviceRepository.deleteByMMac(mMac);
            deviceIndex.delete(mMac, device.getId());
            return "移除成功";
        }
        return "无记录";
    }
}
