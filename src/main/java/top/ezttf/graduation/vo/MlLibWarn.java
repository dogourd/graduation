package top.ezttf.graduation.vo;

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
public class MlLibWarn implements Serializable {

    private double time;
    private double count;
    private double random;

}
