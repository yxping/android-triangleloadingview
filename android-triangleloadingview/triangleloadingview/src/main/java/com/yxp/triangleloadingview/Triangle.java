package com.yxp.triangleloadingview;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;

/**
 * Created by yxp on 2015/11/7.
 */
public class Triangle{
    public final static float TAN30 = 0.5773f;
    // the shift of this triangle in the canvas after init
    public final static int SHIFT = -TriangleLoadingView.HEIGHT_DEFAULT;
    // the moving distance to the top of this triangle
    private int dev = SHIFT;
    // four point of the triangle include three vertex and a vertex of fan-shaped
    private int ax;
    private int ay;
    private int bx;
    private int by;
    private int cx;
    private int cy;
    private int dx;
    private int dy;
    // triangle color and fan-shaped color
    private int bgColor, topColor;
    private Path path;
    // length of triangle
    private int length = 40;
    private int rotateDegree;
    // radian of circular corner
    private int rate = 3;
    private LinearGradient linearGradient;

    public Triangle(int bx, int by, int rotateDegree, int topColor, int bgColor) {
        this.length = TriangleLoadingView.LENGTH;
        this.rate = 3 * length / 40;
        this.ax = bx - length;
        this.ay = by;
        this.bx = bx;
        this.by = by;
        this.cx = bx;
        this.cy = (int) (by + length * TAN30);
        this.dx = bx - (cy - by);
        this.dy = (int) (by + (cy - by) * TAN30);
        this.bgColor = bgColor;
        this.topColor = topColor;
        this.rotateDegree = rotateDegree;
        path = new Path();
        linearGradient = new LinearGradient(bx, by, dx, dy, topColor, bgColor, Shader.TileMode.CLAMP);
    }

    public void drawSelf(Canvas canvas, Paint paint){
        // draw triangle with round corner
        paint.setColor(bgColor);
        path.moveTo(ax + rate, ay);
        path.lineTo(bx - rate, by);
        path.quadTo(bx, by, cx, by + rate);
        path.lineTo(cx, cy - rate);
        path.quadTo(cx, cy, cx - rate, cy - rate * TAN30);
        path.quadTo(ax, ay, ax + rate, ay);
        path.close();
        canvas.save();
        canvas.translate(0, dev);
        canvas.rotate(rotateDegree, bx, by);
        canvas.drawPath(path, paint);
        // draw fan-shaped with round corner
        path.reset();
        path.moveTo(cx, cy - rate);
        path.lineTo(cx, by + rate / 2);
        path.quadTo(cx - length * TAN30 * TAN30, by, cx - length * TAN30 * TriangleLoadingView.SIN60, cy - length * TAN30 * 0.5f);
        path.lineTo(cx - rate, cy - rate * TAN30);
        path.quadTo(cx, cy, cx, cy - rate);
        path.close();
        paint.setShader(linearGradient);
        paint.setColor(Color.GRAY);
        canvas.drawPath(path, paint);
        canvas.restore();
        paint.setShader(null);
    }

    public void moveVertically(int des){
        dev = des;
    }

    public void moveUp(){
        dev -= 3;
    }

    public void reset(){
        dev = SHIFT;
    }
    public void setColor(int topColor, int bgColor) {
        this.bgColor = bgColor;
        this.topColor = topColor;
        linearGradient = new LinearGradient(bx, by, dx, dy, topColor, bgColor, Shader.TileMode.CLAMP);
    }

    public int getBy() {
        return by;
    }

    public int getDev() {
        return dev;
    }

    public void setDev(int dev) {
        this.dev = dev;
    }
}
