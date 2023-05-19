public class vector3 {
    private float x;
    private float y;
    private float z;

    public float getX(){ return x; }
    public float getY(){ return y; }
    public float getZ(){ return z; }

    public vector3(float x, float y, float z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    public vector3(double x, double y, double z)
    {
        this.x = (float)x;
        this.y = (float)y;
        this.z = (float)z;
    }

    public String toString()
    {
        return "X: "+x+", Y: "+y+", Z: "+z;
    }

    public float[][] toMatrix()
    {
        return new float[][] {{ x }, { y }, { z }};
    }
}
