package top.ezttf.graduation.vo;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import top.ezttf.graduation.pojo.UserInfo;

import java.util.Date;
import java.util.List;

/**
 * @author yuwen
 * @date 2019/3/5
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReceiveData {

    @JsonProperty(value = "id")
    private String id;

    @JsonProperty(value = "data")
    @JSONField(name = "data")
    private List<UserInfo> userInfos;

    @JsonProperty(value = "mmac")
    private String mmac;

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
    @JSONField(name = "time", format = "EEE MMM dd HH:mm:ss yyyy")
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
}
