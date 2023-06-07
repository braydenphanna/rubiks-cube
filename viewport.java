import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class viewport extends JPanel{
    int faceIndex =0;
    boolean initialized = false;
    Point startPoint = new Point(0,0);
    public static double[][] pointPositions = new double[client.points.length][2];
    int farthestZIndex = 6 ;
    int closestZIndex = 0 ;
    face[] faces = {new face(Color.RED, "red"),new face(Color.BLUE, "blue"),new face(Color.YELLOW, "yellow"),new face(Color.ORANGE, "orange"),new face(Color.WHITE, "white"),new face(Color.GREEN, "green")};

    public viewport(){
        setBackground(Color.black);
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
        System.out.println("________________");
        for(int j =0; j<faces.length; j++)
        {
            faces[j].update();
            g2.setColor(faces[j].getColor());
            //DEBUG LINE System.out.println(j + " corresponds with " + faces[j] + " | " +faces[j].getColorString() +" | Avg Pos: " + faces[j].getAvgPointPos() + " | Keys: " +faces[j].getKeys() );
            g2.setStroke(new BasicStroke(1));
            g2.fillPolygon(faces[j]);
            g2.setStroke(new BasicStroke(4));
            g2.setColor(Color.black);
            g2.drawPolygon(faces[j]);
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
