package top.ezttf.graduation.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 采集到的用户信号信息实体
 *
 * @author yuwen
 * @date 2018/12/23
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserInfo {

    /**
     * 客户手机 mac 地址
     */
    @JsonProperty(value = "mac")
    private String mac;

    /**
     * 客户设备针对于收集其信息的嗅探设备的信号强度
     */
    @JsonProperty(value = "rssi")
    private Integer rssi;

    /**
     * 信道
     */
    @JsonProperty(value = "ch")
    private Integer ch;

    /**
     * 客户手机与嗅探设备的距离  (分米)
     */
    @JsonProperty(value = "range")
    private Double range;


    /**
     * 客户手机所连接的wifi热点名称, 可能为 null
     */
    @JsonProperty(value = "ts")
    private String ts;

    /**
     * 客户手机所连接的wifi热点的 mac 地址
     */
    @JsonProperty(value = "tmc")
    private String tmc;

    /**
     * 客户手机是否连接路由器   连接为"Y",否则为"N", 当且仅当该值为"Y", ts字段以及tmc字段才会有值
     * 该值可能为 null, null默认为N
     */
    @JsonProperty(value = "tc")
    private String tc;

    /**
     * 客户手机是否处于睡眠状态
     */
    @JsonProperty(value = "ds")
    private String ds;

    /**
     * 客户端连接的无线名称
     */
    @JsonProperty(value = "router")
    private String router;
}
