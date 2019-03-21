package top.ezttf.graduation.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
}
