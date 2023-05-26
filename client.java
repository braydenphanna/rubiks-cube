import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class client{
    public static vector3[] points;
    public static vector3[] pointsRotated;
    public static void main(String[] args)
    {
        Frame f = new Frame();
        f.setSize(800,800);

        points = new vector3[]
        { 
            new vector3(-0.5, -0.5, 0.5),
            new vector3(0.5, -0.5, 0.5),
            new vector3(0.5, 0.5, 0.5),
            new vector3(-0.5, 0.5, 0.5),
            new vector3(-0.5, -0.5, -0.5),
            new vector3(0.5, -0.5, -0.5),
            new vector3(0.5, 0.5, -0.5),
            new vector3(-0.5, 0.5, -0.5)
        };
        
        pointsRotated = new vector3[points.length];
        for(int i = 0; i<points.length; i++)
        {
            pointsRotated[i] = points[i];
        }
        
        viewport v = new viewport();
        f.add(v);

        int[][][] rubiks = {{{0,0,0}, 
                             {0,0,0}, 
                             {0,0,0}},
                            {{1,1,1}, 
                             {1,1,1}, 
                             {1,1,1}},
                            {{2,2,2}, 
                             {2,2,2}, 
                             {2,2,2}},
                            {{3,3,3}, 
                             {3,3,3}, 
                             {3,3,3}},
                            {{4,4,4}, 
                             {4,4,4},
                             {4,4,4}},
                            {{5,5,5}, 
                             {5,5,5}, 
                             {5,5,5}}};
        
        f.setVisible(true);

        ActionListener taskPerformer = new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                }
            };
        Timer t = new Timer(25, taskPerformer);
        t.start();

    }

    public static double[][] matrixMultiply(double[][] a, double[][] b)
    {
        double[][] result = new double[a.length][b[0].length];
        for(int i = 0; i<a.length; i++)
        {
            for(int j = 0; j<b[0].length; j++)
            {
                for(int k = 0; k<b.length; k++)
                {
                    result[i][j] += a[i][k] *b[k][j];    
                }        
            }
        }
        return result;
    }

    public static void print2dArr(float[][] f)
    {
        for(int i =0; i<f.length; i++)
        {
            for(int j =0; j<f[0].length; j++)
            {
                System.out.print(f[i][j] +", ");
            }

            System.out.print("\n");
        }
    }
}