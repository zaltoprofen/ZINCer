package com.fruitsandwich.zincer.opengl;

import java.nio.FloatBuffer;

/**
 * Created by nakac on 15/07/24.
 */
public class Vec3 {
    float x, y, z;

    public Vec3(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vec3() {
    }

    public float[] asArray() {
        return new float[]{x, y, z};
    }

    public Vec3 appendTo(FloatBuffer buffer) {
        buffer.put(new float[]{x, y, z});
        return this;
    }

    public float distance(Vec3 v) {
        double dx = x - v.x;
        double dy = y - v.y;
        double dz = z - v.z;
        return (float)Math.sqrt(dx*dx + dy*dy + dz*dz);
    }

    public Vec3 subtract(Vec3 v){
        return new Vec3(x - v.x, y - v.y, z - v.z);
    }

    public Vec3 outer(Vec3 v) {
        return new Vec3(
                y * v.z - z * v.y,
                z * v.x - x * v.z,
                x * v.y - y * v.x);
    }

    public double norm() {
        return Math.sqrt(x*x + y*y + z*z);
    }

    public double angleRad(Vec3 v) {
        double ip = x * v.x + y * v.y + z * v.z;
        return Math.acos(ip/(norm() * v.norm()));
    }
}
