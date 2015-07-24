package com.fruitsandwich.zincer.opengl;

import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by nakac on 15/07/24.
 */
public class Cylinder {
    private Vec3 topCenter;
    private Vec3 bottomCenter;
    private float radius;
    private int nVert = 30;

    private FloatBuffer top;
    private FloatBuffer side;
    private FloatBuffer bottom;

    private Vec3 outer;
    private double angle;

    public Cylinder(Vec3 topCenter, Vec3 bottomCenter, float radius) {
        this.topCenter = topCenter;
        this.bottomCenter = bottomCenter;
        this.radius = radius;
        build();
    }

    public void draw(GL10 gl, Color c) {
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glTranslatef(topCenter.x, topCenter.y, topCenter.z);
        gl.glRotatef((float)angle, outer.x, outer.y, outer.z);

        gl.glFrontFace(GL10.GL_CCW);
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_AMBIENT, c.asArray(), 0);

        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, top);
        gl.glDrawArrays(GL10.GL_TRIANGLE_FAN, 0, nVert + 2);

        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, bottom);
        gl.glDrawArrays(GL10.GL_TRIANGLE_FAN, 0, nVert + 2);

        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, side);
        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 2 * nVert + 2);
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
    }

    private void build() {
        double dt = 2 * Math.PI / nVert;
        top = ByteBuffer.allocateDirect(12 * (nVert + 2))
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        bottom = ByteBuffer.allocateDirect(12 * (nVert + 2))
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        side = ByteBuffer.allocateDirect(12 * (nVert * 2 + 2))
                .order(ByteOrder.nativeOrder()).asFloatBuffer();

        // TODO: 法線ベクトルの計算
        float dx = topCenter.distance(bottomCenter);
        top.put(new float[]{0, 0, 0});
        bottom.put(new float[]{dx, 0, 0});
        for (int i = 0; i < nVert; i++) {
            double y = radius * Math.cos(i * dt);
            double z = radius * Math.sin(i * dt);
            Vec3 t = new Vec3(0, (float)y, (float)z);
            Vec3 b = new Vec3(dx, (float)y, (float)z);
            t.appendTo(top).appendTo(side);
            b.appendTo(bottom).appendTo(side);
        }
        new Vec3(0, radius, 0).appendTo(top).appendTo(side);
        new Vec3(dx, radius, 0).appendTo(bottom).appendTo(side);
        top.position(0);
        bottom.position(0);
        side.position(0);

        Vec3 d = bottomCenter.subtract(topCenter);
        Vec3 v = new Vec3(dx, 0, 0);

        outer = v.outer(d);
        angle = 180 * d.angleRad(v) / Math.PI;
    }
}
