package top.ezttf.graduation.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

/**
 * @author yuwen
 * @date 2019/4/27
 */
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tb_device")
public class Device {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Basic
    @Column(name = "m_mac", nullable = false)
    private String mMac;

    @Basic
    @Column(name = "geo", nullable = false)
    private String geo;

    @Basic
    @Column(name = "create_time", nullable = false)
    private Date createTime;

    @Basic
    @Column(name = "update_time", nullable = false)
    private Date updateTime;

    public Device(String mMac, String geo) {
        this.mMac = mMac;
        this.geo = geo;
        this.createTime = new Date();
        this.updateTime = this.createTime;
    }
}
