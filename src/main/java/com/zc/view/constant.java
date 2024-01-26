package com.zc.view;

/**
 * 2024-01-23
 * zhangxl
 */
public class constant {
    public static int leftIntr = 10;
    public static int bottomIntr = 6;
    public static int topIntr = 40 + bottomIntr;

    public static int w_num = 16;
    public static int h_num = 10;

    public static int cellWidth = 15;
    public static int cellHeight = cellWidth;

    public static double WIDTH = w_num * cellWidth + leftIntr * 2;
    public static double HEIGHT = h_num * cellHeight + topIntr + bottomIntr;

    public static int bugSpeed = 160;
}
