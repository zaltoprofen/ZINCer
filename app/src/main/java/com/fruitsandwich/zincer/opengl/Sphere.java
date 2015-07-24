package com.fruitsandwich.zincer.opengl;

import com.google.common.collect.Lists;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by nakac on 15/07/23.
 */
public class Sphere {

    float mRadius;
    float cx, cy, cz;
    int nRound;
    static final double DEG = Math.PI/180;

    FloatBuffer bv, tv;
    List<FloatBuffer> ov;

    public Sphere(float cx, float cy, float cz, float radius, double step) {
        this.cx = cx;
        this.cy = cy;
        this.cz = cz;
        mRadius = radius;
        //vertices = ByteBuffer.allocateDirect(30000 * 4)
        //        .order(ByteOrder.nativeOrder()).asFloatBuffer();
        this.nRound = (int)(360.0/step);
        build();
    }

    public void build2() {
    }

    public void draw(GL10 gl, Color c) {
        gl.glFrontFace(GL10.GL_CCW);
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, tv);

        //gl.glColor4f(c.getR(), c.getG(), c.getB(), c.getA());
        float[] ca = c.asArray();
        //gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_SPECULAR, ca, 0);
        gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_DIFFUSE, ca, 0);
        gl.glDrawArrays(GL10.GL_TRIANGLE_FAN, 0, 1 + nRound);
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);

        for (FloatBuffer fb : ov) {
            gl.glFrontFace(GL10.GL_CW);
            gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
            gl.glVertexPointer(3, GL10.GL_FLOAT, 0, fb);

            //gl.glColor4f(c.getR(), c.getG(), c.getB(), c.getA());
            gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 2 * nRound + 2);
            gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
        }

        gl.glFrontFace(GL10.GL_CCW);
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, bv);

        //gl.glColor4f(c.getR(), c.getG(), c.getB(), c.getA());
        gl.glDrawArrays(GL10.GL_TRIANGLE_FAN, 0, 1 + nRound);
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
    }

    private void build(){
        double dTheta = 2*Math.PI/nRound;
        double dPhi = dTheta;

        Vec3 top = new Vec3(cx, cy, cz - mRadius);
        Vec3 bottom = new Vec3(cx, cy, cz + mRadius);
        List<List<Vec3>> vertices = Lists.newArrayList();
        for(int p = 1; p < nRound - 1; p++) {
            double phi = p * dPhi - Math.PI;
            List<Vec3> fb = Lists.newArrayListWithCapacity(nRound);
            for(int t = 0; t < nRound; t++) {
                double theta = t * dTheta;
                Vec3 v = new Vec3();
                v.x = ((float) (mRadius * Math.sin(phi) * Math.cos(theta)) + cx);
                v.y = ((float) (mRadius * Math.sin(phi) * Math.sin(theta)) + cy);
                v.z = ((float) (mRadius * Math.cos(phi)) + cz);
                fb.add(v);
            }
            vertices.add(fb);
        }

        // TODO: 法線ベクトルの計算
        tv = ByteBuffer.allocateDirect(12 * (1 + nRound))
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        top.appendTo(tv);
        for (Vec3 v : vertices.get(0)) v.appendTo(tv);
        tv.position(0);

        ov = Lists.newArrayList();
        for (int i = 0; i < nRound - 3; i++) {
            FloatBuffer V = ByteBuffer.allocateDirect(12 * (2 * nRound + 2))
                    .order(ByteOrder.nativeOrder()).asFloatBuffer();
            for (int j = 0; j < nRound; j++) {
                vertices.get(i).get(j).appendTo(V);
                vertices.get(i + 1).get(j).appendTo(V);
            }
            vertices.get(i).get(0).appendTo(V);
            vertices.get(i + 1).get(0).appendTo(V);
            V.position(0);
            ov.add(V);
        }

        bv = ByteBuffer.allocateDirect(12 * (1 + nRound))
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        bottom.appendTo(bv);
        for (Vec3 v : vertices.get(nRound - 3)) v.appendTo(bv);
        bv.position(0);
    }
}
