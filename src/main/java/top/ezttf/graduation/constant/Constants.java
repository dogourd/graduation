package top.ezttf.graduation.constant;

/**
 * @author yuwen
 * @date 2019/3/20
 */

/**
 * 常量定义
 */
public class Constants {

    /**
     * WIFI HBase Table
     */
    public class WifiTable {
        /**
         * WIFI Table表名
         */
        public static final String TABLE_NAME = "graduation:wifi";

        /**
         * 设备信息列族
         */
        public static final String FAMILY_D = "d";

        /**
         * 设备id
         */
        public static final String ID = "id";

        /**
         * 设备mac地址
         */
        public static final String MMAC = "mmac";

        /**
         * 手机用户信息列族
         */
        public static final String FAMILY_U = "u";

        /**
         * 手机用户mac地址
         */
        public static final String MAC = "mac";

        /**
         * 手机用户和嗅探设备距离
         */
        public static final String RANGE = "range";

        /**
         * 收集时间信息列族
         */
        public static final String FAMILY_T = "t";

        /**
         * 收集时间
         */
        public static final String TIME = "time";
    }


    /**
     * Warn HBase Table
     */
    public class WarnTable {

        /**
         * Warn Table表名
         */
        public static final String TABLE_NAME = "graduation:warn";

        /**
         * 设备信息列族
         */
        public static final String FAMILY_D = "d";

        /**
         * 设备id
         */
        public static final String ID = "id";

        /**
         * 设备mac地址
         */
        public static final String MMAC = "mmac";

        /**
         * 数据信息列族
         */
        public static final String FAMILY_I = "i";

        /**
         * 一轮数据的data数组长度
         */
        public static final String COUNT = "count";

        /**
         * 时间信息列族
         */
        public static final String FAMILY_T = "t";

        /**
         * 收集时间
         */
        public static final String TIME = "time";

    }
}
