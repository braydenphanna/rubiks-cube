import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

public class viewport extends JPanel{
    int faceIndex =0;
    boolean initialized = false;
    Point startPoint = new Point(0,0);
    public static double[][] pointPositions = new double[client.points.length][2];
    int farthestZIndex = 6 ;
    int closestZIndex = 0 ;
    face[] faces = {new face(0),new face(1),new face(2),new face(3),new face(4),new face(5)};
    rubiks r;
    public viewport(){
        setBackground(Color.black);
        r = new rubiks();
        faceIndex =0;
        for(int i = 0; i<client.points.length; i++)
        {
            boolean x = client.points[closestZIndex].getX() == client.points[i].getX();
            boolean y = client.points[closestZIndex].getY() == client.points[i].getY();
            boolean z = client.points[closestZIndex].getZ() == client.points[i].getZ();
            if((!x && y && z) || (x && !y && z) || (x && y && !z))
            {
                faces[faceIndex].addPointKey(closestZIndex);
                faces[faceIndex].addPointKey(i);
                for(int j = 0; j<client.points.length; j++)
                {
                    if(checkAdjacency(x, y, z, i, j))
                    {
                        faces[faceIndex].addPointKey(j);
                    }
                }
                for(int j = 0; j<client.points.length; j++)
                {
                    if(checkAdjacency(x, y, z, closestZIndex, j))
                    {
                        faces[faceIndex].addPointKey(j);
                    }
                }
                faceIndex++;
            }
        }
        for(int i = 0; i<client.points.length; i++)
        {
            boolean x = client.points[farthestZIndex].getX() == client.points[i].getX();
            boolean y = client.points[farthestZIndex].getY() == client.points[i].getY();
            boolean z = client.points[farthestZIndex].getZ() == client.points[i].getZ();
            if((!x && y && z) || (x && !y && z) || (x && y && !z))
            {
                faces[faceIndex].addPointKey(farthestZIndex);
                faces[faceIndex].addPointKey(i);
                for(int j = 0; j<client.points.length; j++)
                {
                    if(checkAdjacency(x, y, z, i, j))
                    {
                        faces[faceIndex].addPointKey(j);
                    }
                }
                for(int j = 0; j<client.points.length; j++)
                {
                    if(checkAdjacency(x, y, z, farthestZIndex, j))
                    {
                        faces[faceIndex].addPointKey(j);
                    }
                }
                faceIndex++;
            }
        }
        this.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent evt) {
                if(evt.isShiftDown())
                {
                    startPoint = new Point(evt.getX(), evt.getY());
                }
            }
        });
        this.addMouseMotionListener(new MouseAdapter() {
            public void mouseDragged(MouseEvent evt) {
                if(evt.isShiftDown())
                {
                    double deltaX = -(evt.getY()-startPoint.getY());
                    double deltaY = evt.getX()-startPoint.getX();
                    
                     double rotSpeed = 0.01;

                    // Rotate around x-axis
                    double[][] xRotationMatrix = {
                        {1, 0, 0},
                        {0, Math.cos(deltaX * rotSpeed), -Math.sin(deltaX * rotSpeed)},
                        {0, Math.sin(deltaX * rotSpeed), Math.cos(deltaX * rotSpeed)}
                    };
                    
                    // Rotate around y-axis
                    double[][] yRotationMatrix = {
                        {Math.cos(deltaY * rotSpeed), 0, Math.sin(deltaY * rotSpeed)},
                        {0, 1, 0},
                        {-Math.sin(deltaY * rotSpeed), 0, Math.cos(deltaY * rotSpeed)}
                    };

                    double[][] rotationMatrix = client.matrixMultiply(xRotationMatrix, yRotationMatrix);

                    double closestZ = Integer.MIN_VALUE;
                    double farthestZ = Integer.MAX_VALUE;
                    for (int i =0; i<client.points.length; i++) {
                        double[][] rotated = client.matrixMultiply(rotationMatrix, client.pointsRotated[i].toMatrix());
                        
                        client.pointsRotated[i] = new vector3(rotated[0][0], rotated[1][0], rotated[2][0]);
                        
                        double depth = 1/ (2- rotated[2][0]);
                        double[][] projection =
                        {
                            {depth,0,0},
                            {0,depth,0}
                        };
                        double[][] pos = client.matrixMultiply(projection, rotated);
                        pointPositions[i][0] = pos[0][0] *600 + getWidth()/2;
                        pointPositions[i][1] = pos[1][0] *600 + getHeight()/2;

                        if(rotated[2][0] > closestZ)
                        {
                            closestZ = rotated[2][0];
                            closestZIndex = i;
                        }
                        else if(rotated[2][0] < farthestZ)
                        {
                            farthestZ = rotated[2][0];
                            farthestZIndex = i;
                        }
                    } 
                    repaint();
                    startPoint = evt.getPoint();
                }
                
            }
        });
    }
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        
        int n = faces.length;  
        for(int i=0; i < n; i++){  
            for(int j=1; j < (n-i); j++){  
                if( faces[j-1].getAvgPointPos()> faces[j].getAvgPointPos()){
                    face temp = faces[j-1];  
                    faces[j-1] = faces[j];  
                    faces[j] = temp;  
                }        
            }  
        }  
        for(int j =0; j<faces.length; j++)
        {
            faces[j].update();
            g2.setColor(Color.lightGray);
            //DEBUG LINE System.out.println(j + " corresponds with " + faces[j] + " | " +faces[j].getColorString() +" | Avg Pos: " + faces[j].getAvgPointPos() + " | Keys: " +faces[j].getKeys() );
            g2.setStroke(new BasicStroke(1));
            g2.fillPolygon(faces[j]);
            g2.setColor(Color.black);
            g2.setStroke(new BasicStroke(6));
            g2.drawPolygon(faces[j]);
            
            ArrayList<Integer> keys = faces[j].getKeys();
            
            /*g2.setColor(Color.blue);
            for(int t= 0; t<4; t++)
            {
                int r = t+1;
                if(r>=4) r-=4;
                
                g2.fillOval((int)((pointPositions[keys.get(t)][0] + pointPositions[keys.get(r)][0])/2), 
                         (int)((pointPositions[keys.get(t)][1] + pointPositions[keys.get(r)][1])/2), 40,40);   
            }*/
                    
            g2.setColor(Color.black);
            g2.setStroke(new BasicStroke(6));
            g2.drawLine((int)((pointPositions[keys.get(0)][0] + pointPositions[keys.get(1)][0])/2), 
                         (int)((pointPositions[keys.get(0)][1] + pointPositions[keys.get(1)][1])/2), 
                         (int)((pointPositions[keys.get(2)][0] + pointPositions[keys.get(3)][0])/2), 
                         (int)((pointPositions[keys.get(2)][1] + pointPositions[keys.get(3)][1])/2));
            g2.drawLine((int)((pointPositions[keys.get(1)][0] + pointPositions[keys.get(2)][0])/2), 
                         (int)((pointPositions[keys.get(1)][1] + pointPositions[keys.get(2)][1])/2), 
                         (int)((pointPositions[keys.get(3)][0] + pointPositions[keys.get(0)][0])/2), 
                         (int)((pointPositions[keys.get(3)][1] + pointPositions[keys.get(0)][1])/2));    
            faces[j].reset();
        }
    }
    
    public boolean checkAdjacency(boolean x, boolean y, boolean z, int i, int j)
    {
        if(((!x) && client.points[i].getX() == client.points[j].getX()&&
        client.points[i].getY() == client.points[j].getY()&&
        client.points[i].getZ() != client.points[j].getZ()) 
        ||
        ((!y) && client.points[i].getX() != client.points[j].getX()&&
        client.points[i].getY() == client.points[j].getY()&&
        client.points[i].getZ() == client.points[j].getZ()) 
        ||
        ((!z) && client.points[i].getX() == client.points[j].getX()&&
        client.points[i].getY() != client.points[j].getY()&&
        client.points[i].getZ() == client.points[j].getZ()))
        {
            return true;
        }
        return false;
    }
}
