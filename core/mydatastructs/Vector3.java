package core.mydatastructs;

import java.awt.Graphics;

public class Vector3 extends VECTOR_CONSTANTS {
    public double x, y, z;

    public Vector3(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3() {
        this.x = 0;
        this.y = 0;
        this.z = 0;
    }

    /*
     * Copy
     */

    public Vector3 copy() {
        return new Vector3(this.x, this.y, this.z);
    }

    /*
     * Add Methods
     */

    public Vector3 add(Vector3 v) {
        return new Vector3(x + v.x, y + v.y, z + v.z);
    }

    public Vector3 add(double x, double y, double z) {
        return new Vector3(this.x + x, this.y + y, this.z + z);
    }

    public Vector3 add(double val) {
        return new Vector3(x + val, y + val, z + val);
    }

    /*
     * subtracttract
     */

    public Vector3 sub(Vector3 v) {
        return new Vector3(x - v.x, y - v.y, z - v.z);
    }

    public Vector3 sub(double x, double y, double z) {
        return new Vector3(this.x - x, this.y - y, this.z - z);
    }

    public Vector3 sub(double val) {
        return new Vector3(x - val, y - val, z - val);
    }

    /*
     * multiplyiply Methods
     */

    public Vector3 mult(Vector3 v) {
        return new Vector3(x * v.x, y * v.y, z * v.z);
    }

    public Vector3 mult(double x, double y, double z) {
        return new Vector3(this.x * x, this.y * y, this.z * z);
    }

    public Vector3 mult(double val) {
        return new Vector3(x * val, y * val, z * val);
    }

    /*
     * divideide methods
     */

    public Vector3 div(Vector3 v) {
        return new Vector3(x / v.x, y / v.y, z / v.z);
    }

    public Vector3 div(double x, double y, double z) {
        return new Vector3(this.x / x, this.y / y, this.z / z);
    }

    public Vector3 div(double val) {
        return new Vector3(x / val, y / val, z / val);
    }

    /*
     * Dot Product Method
     */

    public static double dot(Vector3 v1, Vector3 v2) {
        return (v1.x * v2.x) + (v1.y * v2.y) + (v1.z * v2.z);
    }

    /*
     * Magnitude Math
     */

    public double mag() {
        return Math.sqrt(x * x + y * y + z * z);
    }

    public double sqrMag() {
        return x * x + y * y + z * z;
    }

    /*
     * Distance methods
     */

    public static double dist(Vector3 v1, Vector3 v2) {
        double x = v1.x - v2.x;
        double y = v1.y - v2.y;
        double z = v1.z - v2.z;
        return Math.sqrt(x * x + y * y + z * z);
    }

    public static double sqrDist(Vector3 v1, Vector3 v2) {
        double x = v1.x - v2.x;
        double y = v1.y - v2.y;
        double z = v1.z - v2.z;
        return x * x + y * y + z * z;
    }

    /*
     * Normalize Method
     */

    public Vector3 norm() {
        double mag = mag();
        return new Vector3(x / mag, y / mag, z / mag);
    }

    /*
     * Equivalency Check Methods
     */

    public boolean equals(double x, double y, double z) {
        return this.x == x && this.y == y && this.z == z;
    }

    public boolean equals(Vector3 v) {
        return this.x == v.x && this.y == v.y && this.z == v.z;
    }

    public static void drawLine2D(Graphics g, Vector3 v1, Vector3 v2) {
        g.drawLine((int) v1.x, (int) v1.y, (int) v2.x, (int) v2.y);
    }

    /*
     * To String Method
     */

    public String toString() {
        return String.format("Vector3[%.4f, %.4f, %.4f]", this.x, this.y, this.z);
    }

    public String toString(int numSigFigs) {
        String component = "%." + numSigFigs + "f";
        String components = component + ", " + component + ", " + component;

        return String.format("Vector3[" + components + "]", this.x, this.y, this.z);
    }
}