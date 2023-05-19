import java.awt.*;
import javax.swing.*;

public class viewport extends JPanel{
    double angle = 0;
    Color[] colors = new Color[6];
    public viewport(){
        setBackground(Color.black);
        colors[0] = Color.red;
        colors[1] = Color.blue;
        colors[2] = Color.yellow;
        colors[3] = Color.white;
        colors[4] = Color.green;
        colors[5] = Color.ORANGE;
    }
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(Color.magenta);
        //g2.setColor(new Color((int)(Math.random()*255),(int)(Math.random()*255), (int)(Math.random()*255)));

        int scale = (int)(1.5 * Math.min(getWidth()/2, getHeight()/2));

        float[][] rotationX =
        {
            {1,0,0},
            {0,(float)Math.cos(angle),(float)-Math.sin(angle)},
            {0,(float)Math.sin(angle),(float)Math.cos(angle)}
        };
        float[][] rotationY =
        {
            {(float)Math.cos(angle),0,(float)Math.sin(angle)},
            {0,1,0},
            {(float)-Math.sin(angle),0, (float)Math.cos(angle)}
        };
        float[][] rotationZ =
        {
            {(float)Math.cos(angle),(float)-Math.sin(angle),0},
            {(float)Math.sin(angle),(float)Math.cos(angle),0},
            {0,0,1}
        };

        float[][] pointPositions = new float[client.points.length][2];

        
        int closestZIndex = 0 ;
        float closestZ = Integer.MIN_VALUE;
        for (int i =0; i<client.points.length; i++) {
            float[][] rotated = client.matrixMultiply(rotationX, client.points[i].toMatrix());
            rotated = client.matrixMultiply(rotationY, rotated);
            rotated = client.matrixMultiply(rotationZ, rotated);

            float distance = 2;
            float z = 1/ (distance- rotated[2][0]);
            float[][] projection2 =
            {
                {z,0,0},
                {0,z,0}
            };

            float[][] pos = client.matrixMultiply(projection2, rotated);
            pointPositions[i][0] = pos[0][0] *scale + getWidth()/2;
            pointPositions[i][1] = pos[1][0] *scale + getHeight()/2;

            if(rotated[2][0] > closestZ)
            {
                closestZ = rotated[2][0];
                closestZIndex = i;
            }
        }  
        g2.setColor(Color.green);
        g2.fillOval((int)(pointPositions[closestZIndex][0]),(int)(pointPositions[closestZIndex][1]), 10, 10);
          
        Polygon[] faces = {new Polygon(),new Polygon(),new Polygon()};
        int faceIndex = 0;
        for(int i = 0; i<client.points.length; i++)
        {

            boolean x = client.points[closestZIndex].getX() == client.points[i].getX();
            boolean y = client.points[closestZIndex].getY() == client.points[i].getY();
            boolean z = client.points[closestZIndex].getZ() == client.points[i].getZ();

            if((!x && y && z) || (x && !y && z) || (x && y && !z))
            {
                g2.fillOval((int)(pointPositions[i][0]),(int)(pointPositions[i][1]), 10, 10);
                faces[faceIndex].addPoint((int)(pointPositions[closestZIndex][0]),(int)(pointPositions[closestZIndex][1]));
                faces[faceIndex].addPoint((int)(pointPositions[i][0]),(int)(pointPositions[i][1]));
                for(int j = 0; j<client.points.length; j++)
                {
                    if(checkAdjacency(x, y, z, i, j))
                    {
                        faces[faceIndex].addPoint((int)pointPositions[j][0],(int)pointPositions[j][1]); 
                    }
                }
                for(int j = 0; j<client.points.length; j++)
                {
                    if(checkAdjacency(x, y, z, closestZIndex, j))
                    {
                        faces[faceIndex].addPoint((int)pointPositions[j][0],(int)pointPositions[j][1]); 
                    }
                }
                faceIndex++;
            }
        }

        //this is really bad. figure out a better way to draw the sides, so that they don't draw in the wrong order

        double biggestArea = Math.max(faces[0].getBounds().getWidth()*faces[0].getBounds().getHeight(),
            Math.max(faces[1].getBounds().getWidth()*faces[1].getBounds().getHeight(),
                     faces[2].getBounds().getWidth()*faces[2].getBounds().getHeight()));
        int maxIndex = 0;
        for(int j =0; j<faces.length; j++)
        {
            if(faces[j].getBounds().getWidth()*faces[j].getBounds().getHeight() != biggestArea)
            {
                    
                g2.setColor(Color.cyan);
                g2.setStroke(new BasicStroke(1));
                g2.fillPolygon(faces[j]);
                g2.setStroke(new BasicStroke(4));
                g2.setColor(Color.black);
                g2.drawPolygon(faces[j]);
            }
            else
            { 
                maxIndex = j;
            }
        }
            
        g2.setColor(Color.red);
        g2.setStroke(new BasicStroke(1));
        g2.fillPolygon(faces[maxIndex]);
        g2.setStroke(new BasicStroke(4));
        g2.setColor(Color.black);
        g2.drawPolygon(faces[maxIndex]);

        angle+=0.01;
    }
    
    public boolean checkAdjacency(boolean x, boolean y, boolean z, int i, int j)
    {
        if(((!x) && client.points[i].getX() == client.points[j].getX()&&
        client.points[i].getY() == client.points[j].getY()&&
        client.points[i].getZ() != client.points[j].getZ()) ||
        (!y) && client.points[i].getX() != client.points[j].getX()&&
        client.points[i].getY() == client.points[j].getY()&&
        client.points[i].getZ() == client.points[j].getZ() ||
        (!z) && client.points[i].getX() == client.points[j].getX()&&
        client.points[i].getY() != client.points[j].getY()&&
        client.points[i].getZ() == client.points[j].getZ())
        {
            return true;
        }
        return false;
    }
}
