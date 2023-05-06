package mydatastructs;

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

    public void add(Vector3 v) {
        this.x += v.x;
        this.y += v.y;
        this.z += v.z;
    }

    public void add(double x, double y, double z) {
        this.x += x;
        this.y += y;
        this.z += z;
    }

    public void add(double num) {
        this.x += num;
        this.y += num;
        this.z += num;
    }

    /*
     * subtracttract
     */

    public void subtract(Vector3 v) {
        this.x -= v.x;
        this.y -= v.y;
        this.z -= v.z;
    }

    public Vector3 subtract(Vector3 v1, Vector3 v2) {
        return new Vector3(v1.x - v2.x, v1.y - v2.y, v1.z - v2.z);
    }

    public void subtract(double x, double y, double z) {
        this.x -= x;
        this.y -= y;
        this.z -= z;
    }

    public void subtract(double num) {
        this.x -= num;
        this.y -= num;
        this.z -= num;
    }

    /*
     * multiplyiply Methods
     */

    public void multiply(Vector3 v) {
        this.x *= v.x;
        this.y *= v.y;
        this.z *= v.z;
    }

    public void multiply(double x, double y, double z) {
        this.x *= x;
        this.y *= y;
        this.z *= z;
    }

    public void multiply(double num) {
        this.x *= num;
        this.y *= num;
        this.z *= num;
    }

    /*
     * divideide methods
     */

    public void divide(Vector3 v) {
        this.x /= v.x;
        this.y /= v.y;
        this.z /= v.z;
    }

    public void divide(double x, double y, double z) {
        this.x /= x;
        this.y /= y;
        this.z /= z;
    }

    public void divide(double num) {
        this.x /= num;
        this.y /= num;
        this.z /= num;
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

    public double magnitude() {
        return Math.sqrt(x * x + y * y + z * z);
    }

    public double sqrMagnitude() {
        return x * x + y * y + z * z;
    }

    /*
     * Distance methods
     */

    public static double distance(Vector3 v1, Vector3 v2) {
        double x = v1.x - v2.x;
        double y = v1.y - v2.y;
        double z = v1.z - v2.z;
        return Math.sqrt(x * x + y * y + z * z);
    }

    /*
     * Normalize Method
     */

    public void normalize() {
        this.divide(magnitude());
    }

    public Vector3 normalized() {
        double mag = magnitude();
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