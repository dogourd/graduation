package top.ezttf.graduation.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * 嗅探设备对应的实体店
 * 例如 嗅探设备id: sldkfsgn;  实体店: 霸王
 *
 * @author yuwen
 * @date 2019/3/6
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tb_district")
public class District {

    /**
     * 自增主键 id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    /**
     * 设备id
     */
    @Basic
    @Column(name = "device_id", nullable = false)
    private String deviceId;


    /**
     * 实体店名
     */
    @Basic
    @Column(name = "district", nullable = false)
    private String district;
}
