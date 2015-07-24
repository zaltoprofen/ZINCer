package com.fruitsandwich.zincer.parser;

import com.fruitsandwich.zincer.opengl.Color;
import com.fruitsandwich.zincer.opengl.Cylinder;
import com.fruitsandwich.zincer.opengl.Sphere;
import com.fruitsandwich.zincer.opengl.Vec3;
import com.fruitsandwich.zincer.util.Utils;
import com.google.common.base.Function;

import java.util.List;
import java.util.Objects;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by nakac on 15/07/23.
 */
public class Mol2 {
    public class GLInfo {
        private Color color;
        private float radius;

        public GLInfo(Color color, float radius) {
            this.color = color;
            this.radius = radius;
        }

        public Color getColor() {
            return color;
        }

        public float getRadius() {
            return radius;
        }
    }

    public enum AtomType {
        C(new Color(0.2f, 0.2f, 0.2f), 0.35f),
        H(new Color(0.8f, 0.8f, 0.8f), 0.2f),
        O(new Color(0.8f, 0.2f, 0.2f), 0.5f),
        N(new Color(0.2f, 0.2f, 0.8f), 0.4f),
        P(new Color(0.2f, 0.8f, 0.2f), 0.7f),
        S(new Color(0.8f, 0.8f, 0.2f), 1.0f),

        Unknown(new Color(0f, 0f, 0f, 1f), 1.0f);

        private Color color;
        private float radius;

        AtomType(Color color, float radius) {
            this.color = color;
            this.radius = radius;
        }

        public Color getColor() {
            return color;
        }

        public float getRadius() {
            return radius;
        }
    }

    public static class Atom {
        private Integer id;
        private String name;
        private float x, y, z;
        private AtomType atomType;
        private Sphere sphere;

        public Atom(Integer id, String name, float x, float y, float z, AtomType atomType) {
            this.id = id;
            this.name = name;
            this.x = x;
            this.y = y;
            this.z = z;
            this.atomType = atomType;
        }

        public void draw(GL10 gl) {
            if (sphere == null) {
                sphere = new Sphere(x, y, z, atomType.getRadius(), 5);
            }

            Color c = atomType.getColor();
            sphere.draw(gl, c);
        }

        public Integer getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public float getX() {
            return x;
        }

        public float getY() {
            return y;
        }

        public float getZ() {
            return z;
        }

        public AtomType getAtomType() {
            return atomType;
        }
    }

    public static class Bond {
        private Integer id;
        private Integer atomId1;
        private Integer atomId2;
        private Cylinder cylinder;

        public Bond(Integer id, Integer atomId1, Integer atomId2) {
            this.id = id;
            this.atomId1 = atomId1;
            this.atomId2 = atomId2;
        }

        public void draw(Mol2 mol2, GL10 gl) {
            if (cylinder == null) {
                Atom a1 = Utils.find(new Function<Atom, Boolean>() {
                    @Override
                    public Boolean apply(Atom a) {
                        return a.getId().equals(atomId1);
                    }
                }, mol2.atoms);
                Atom a2 = Utils.find(new Function<Atom, Boolean>() {
                    @Override
                    public Boolean apply(Atom a) {
                        return a.getId().equals(atomId2);
                    }
                }, mol2.atoms);
                if (a1 != null && a2 != null)
                    cylinder = new Cylinder(
                            new Vec3(a1.x, a1.y, a1.z),
                            new Vec3(a2.x, a2.y, a2.z),
                            0.1f
                    );
            }
            if (cylinder != null) {
                gl.glMatrixMode(GL10.GL_MODELVIEW);
                gl.glLoadIdentity();
                cylinder.draw(gl, new Color(0.7f, 0.7f, 0.7f));
            }
        }
    }

    private List<Atom> atoms;
    private List<Bond> bonds;

    public Mol2() {}

    public Mol2(List<Atom> atoms, List<Bond> bonds) {
        this.atoms = atoms;
        this.bonds = bonds;
    }

    public List<Atom> getAtoms() {
        return atoms;
    }

    public void setAtoms(List<Atom> atoms) {
        this.atoms = atoms;
    }

    public List<Bond> getBonds() {
        return bonds;
    }

    public void setBonds(List<Bond> bonds) {
        this.bonds = bonds;
    }

    public void draw(GL10 gl) {
        for (Atom atom : atoms) {
            atom.draw(gl);
        }
        for (Bond bond : bonds) {
            bond.draw(this, gl);
        }
    }
}
