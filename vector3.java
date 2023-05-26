public class vector3 {
    private double x;
    private double y;
    private double z;

    public double getX(){ return x; }
    public double getY(){ return y; }
    public double getZ(){ return z; }

    public vector3(double x, double y, double z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public String toString()
    {
        return "\nX: "+x+", Y: "+y+", Z: "+z;
    }

    public double[][] toMatrix()
    {
        return new double[][] {{ x }, { y }, { z }};
    }
}
