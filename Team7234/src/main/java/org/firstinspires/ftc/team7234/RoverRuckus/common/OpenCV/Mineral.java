package org.firstinspires.ftc.team7234.RoverRuckus.common.OpenCV;

import android.support.annotation.NonNull;

public abstract class Mineral implements Comparable<Mineral>{

    private int x;
    private int y;
    private int width;
    private int height;

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    /**
     *
     * @param x     Position of the
     * @param y
     * @param width
     * @param height
     */
    Mineral(int x, int y, int width, int height){
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    abstract MineralType getType();

    public void printStatistics(){
        System.out.println("Mineral");
        System.out.println("Type = " + getType());
        System.out.println("x = " + x);
        System.out.println("y = " + y);
        System.out.println("width = " + width);
        System.out.println("height = " + height);
    }

    @Override
    public int compareTo(@NonNull Mineral o) {
        return Integer.compare(this.width, o.getWidth());
    }
}
