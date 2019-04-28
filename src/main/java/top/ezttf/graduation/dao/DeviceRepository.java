package top.ezttf.graduation.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import top.ezttf.graduation.pojo.Device;

/**
 * @author yuwen
 * @date 2019/4/27
 */
public interface DeviceRepository extends JpaRepository<Device, Long> {

    /**
     * 通过设备mac地址查找记录
     * @param mMac mac地址
     * @return device
     */
    Device findByMMac(String mMac);

    /**
     * 通过设备mac地址删除记录
     * @param mMac mac
     */
    void deleteByMMac(String mMac);

}
