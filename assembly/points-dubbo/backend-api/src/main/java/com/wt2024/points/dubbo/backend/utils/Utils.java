package com.wt2024.points.dubbo.backend.utils;

public class Utils {
    /**
     * 判断参数的数据值是否为空
     *
     * @param args 1-N个字符串参数
     * @return 如果有为空的参数，则返回true，否则返回false
     */
    public static boolean isNullData(String... args) {
        for (int i = 0; i < args.length; i++) {
            if (null == args[i] || "".equals(args[i].trim())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断字符串是否非空
     *
     * @param @param  str
     * @param @return true非空
     * @return boolean
     * @throws
     * @Description: TODO
     * @author mhui
     * @date 2016-1-15
     */
    public static boolean notNullStr(String str) {
        if (null != str && !str.trim().equals("")) {
            return true;
        }
        return false;
    }

    /**
     * 功能：产生min-max中的n个不重复的随机数
     * <p>
     * min：产生随机数的最小位置
     * mab：产生随机数的最大位置
     * n: 所要产生多少个随机数
     */
    public static String randomNumber(int min, int max, int n) {
        StringBuffer sb = new StringBuffer();
        //判断是否已经达到索要输出随机数的个数
        if (n > (max - min + 1) || max < min) {
            return null;
        }

        int[] result = new int[n]; //用于存放结果的数组

        int count = 0;
        while (count < n) {
            int num = (int) (Math.random() * (max - min)) + min;
            boolean flag = true;
            for (int j = 0; j < count; j++) {
                if (num == result[j]) {
                    flag = false;
                    break;
                }
            }
            if (flag) {
                result[count] = num;
                count++;
            }
        }

        if (null != result) {
            for (int i = 0; i < result.length; i++) {
                sb.append(result[i] + "");
            }
        }

        return sb.toString();
    }
}
