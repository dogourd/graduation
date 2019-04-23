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

    private double time;
    private double count;
    private double random;


    private double features;

    public Temp(double time, double count, double random) {
        this.time = time;
        this.count = count;
        this.random = random;
    }
}
