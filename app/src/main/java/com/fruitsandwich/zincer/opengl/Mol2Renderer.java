package com.fruitsandwich.zincer.opengl;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;

import com.fruitsandwich.zincer.parser.Mol2;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by nakac on 15/07/24.
 */
public class Mol2Renderer implements GLSurfaceView.Renderer {
    private Mol2 mol2;
    private double distance = 20;
    private double theta = 0;
    private double phi = 0;
    private int height;
    private int width;
    private float centerX = 0, centerY = 0, centerZ = 0;

    public Mol2Renderer() {}

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig eglConfig) {
        GLES20.glClearColor(0.1f, 0.1f, 0.1f, 0.0f);

        gl.glClearDepthf(1.0f);
        gl.glDisable(GL10.GL_BLEND);
        gl.glEnable(GL10.GL_DEPTH_TEST);
        gl.glDepthFunc(GL10.GL_LEQUAL);
        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);
        gl.glShadeModel(GL10.GL_SMOOTH);
        gl.glDisable(GL10.GL_DITHER);

        gl.glLightfv(GL10.GL_LIGHT1, GL10.GL_AMBIENT, new float[]{0.25f, 0.25f, 0.25f, 1.0f}, 0);
        gl.glLightfv(GL10.GL_LIGHT1, GL10.GL_DIFFUSE, new float[]{1.0f, 1.0f, 1.0f, 1.0f}, 0);
        gl.glLightfv(GL10.GL_LIGHT1, GL10.GL_SPECULAR, new float[]{0.8f, 0.8f, 0.8f, 1.0f}, 0);
        gl.glLightfv(GL10.GL_LIGHT1, GL10.GL_POSITION, new float[]{0.0f, 1.0f, 1.0f, 0.0f}, 0);
        gl.glEnable(GL10.GL_LIGHT1);
        gl.glEnable(GL10.GL_LIGHT0);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        this.width = width;
        this.height = height;
        gl.glViewport(0, 0, width, height);
        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadIdentity();
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        gl.glEnable(GL10.GL_LIGHTING);
        double t = Math.PI/180 * theta;
        double p = Math.PI/180 * phi;
        double ps = Math.sin(p) + 0.00001;
        double cx = distance * ps * Math.cos(t) + centerX;
        double cy = distance * ps * Math.sin(t) + centerY;
        double cz = distance * Math.cos(p) + centerZ;
        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadIdentity();
        GLU.gluPerspective(gl, 20, ((float) width) / height, 1, 50);
        GLU.gluLookAt(gl, (float) cx, (float) cy, (float) cz, centerX, centerY, centerZ, 0, 0, 1f);
        //GLU.gluLookAt(gl, 0, 0, 20f, 0, 0, 0, 0, 1f, 0);
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();
        if (mol2 != null) mol2.draw(gl);
    }

    public void setMol2(Mol2 mol2) {
        this.mol2 = mol2;
        double ax = 0.0, ay = 0.0, az = 0.0;
        int nA = mol2.getAtoms().size();
        for (Mol2.Atom atom : mol2.getAtoms()) {
            ax += atom.getX() / nA;
            ay += atom.getY() / nA;
            az += atom.getZ() / nA;
        }
        centerX = (float)ax;
        centerY = (float)ay;
        centerZ = (float)az;
    }

    public double getTheta() {
        return theta;
    }

    public void setTheta(double theta) {
        this.theta = theta;
    }

    public double getPhi() {
        return phi;
    }

    public void setPhi(double phi) {
        this.phi = phi;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
}
