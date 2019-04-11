package top.ezttf.graduation.service;

import org.apache.catalina.LifecycleState;
import top.ezttf.graduation.vo.Wifi;

import java.util.List;

/**
 * @author yuwen
 * @date 2019/3/21
 */
public interface IWifiService {

    void saveWifis(List<Wifi> wifis);
}
