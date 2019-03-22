package top.ezttf.graduation.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

/**
 * 关心的数据实体
 *
 * @author yuwen
 * @date 2019/3/6
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tb_row_data")
public class MySqlRowData {

    /**
     * 主键id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    /**
     * 嗅探设备id
     */
    @Basic
    @Column(name = "device_id", nullable = false)
    private String deviceId;

    /**
     * 嗅探设别mac 地址
     */
    @Basic
    @Column(name = "mmac", nullable = false)
    private String mmac;

    /**
     * 该行记录收集时间
     */
    @Basic
    @Column(name = "start_time", nullable = false)
    private Date startTime;

    /**
     * 手机和嗅探设备距离
     */
    @Basic
    @Column(name = "_range", nullable = false)
    private Double range;

    /**
     * 手机mac地址
     */
    @Basic
    @Column(name = "mac", nullable = false)
    private String mac;

    public MySqlRowData(String deviceId, String mmac, Date startTime, Double range, String mac) {
        this.deviceId = deviceId;
        this.mmac = mmac;
        this.startTime = startTime;
        this.range = range;
        this.mac = mac;
    }
}
