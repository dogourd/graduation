package top.ezttf.graduation.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;

/**
 * @author yuwen
 * @date 2019/3/20
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Warn {

    /**
     * 设备id
     */
    private String id;

    /**
     * 设备mac地址
     */
    private String mmac;

    /**
     * 一轮数据的长度
     */
    private Long count;

    /**
     * 收集时间
     */
    private Date time;

    public Warn assembleFromReceiveData(ReceiveData receiveData) {
        if (receiveData == null) {
            return this;
        }
        if (StringUtils.isNotBlank(receiveData.getId())) {
            this.setId(receiveData.getId());
        }
        if (StringUtils.isNotBlank(receiveData.getMmac())) {
            this.setMmac(receiveData.getMmac());
        }
        if (receiveData.getUserInfos() != null) {
            this.setCount((long) receiveData.getUserInfos().size());
        }
        if (receiveData.getTime() != null) {
            this.setTime(receiveData.getTime());
        }
        return this;
    }
}
