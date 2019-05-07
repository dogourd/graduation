package top.ezttf.graduation.utils;

import java.util.List;

/**
 * @author yuwen
 * @date 2019/5/7
 */
public class CommonUtils {


    /**
     * 若存在目标值则返回目标值, 否则返回和目标值最接近的值
     * @param list
     * @param target
     * @return
     */
    public static Long searchElement(List<Long> list, long target) {
        list.sort(null);
        System.out.println(list);
        int size = list.size();
        long result = 0;
        for (int left = 0, right = size - 1; left < right; ) {
            int midIndex = left + (right - left >> 1);
            long midValue = list.get(midIndex);
            if (midValue == target) {
                return target;
            }
            if (target > midValue) {
                left = midIndex + 1;
            } else {
                right = midIndex - 1;
            }
            result = Math.abs(target - list.get(left)) < Math.abs(target - list.get(right))
                    ? list.get(left)
                    : list.get(right);
        }
        return result;
    }


}
