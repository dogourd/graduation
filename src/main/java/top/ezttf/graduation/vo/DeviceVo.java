package top.ezttf.graduation.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

/**
 * 嗅探设备相关信息实体
 *
 * @author yuwen
 * @date 2018/12/23
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeviceVo {

    /**
     * 嗅探设备id
     */
    @JsonProperty(value = "id")
    private String id;

    /**
     * 嗅探设备自身mac地址
     */
    @JsonProperty(value = "mmac")
    private String deviceMac;

    /**
     * 嗅探设备收集数据后发送频率
     */
    @JsonProperty(value = "rate")
    private Integer rate;

    /**
     * 嗅探设备发送当条数据的时间
     */
    @JsonProperty(value = "time")
    @JsonFormat(pattern = "EEE MMM dd HH:mm:ss yyyy", locale = "US")
    private Date time;

    /**
     * 嗅探设备维度
     */
    @JsonProperty(value = "lat")
    private String lat;

    /**
     * 嗅探设备经度
     */
    @JsonProperty(value = "lon")
    private String lon;

    /**
     * 嗅探设备进行一轮数据收集后，多个用户信息的集合
     */
    @JsonProperty(value = "data")
    private UserInfo[] userInfo;

}
