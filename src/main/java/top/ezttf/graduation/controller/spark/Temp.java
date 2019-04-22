package top.ezttf.graduation.controller.spark;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author yuwen
 * @date 2019/4/22
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Temp implements Serializable {

    private long time;
    private long count;
    private double random;
}
