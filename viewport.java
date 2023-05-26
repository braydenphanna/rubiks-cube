import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class viewport extends JPanel{
    double xangle = 0;
    double yangle = 0;
    double zangle = 0;
    double angle = 0;
    double x = 0; double y =0; double z =0;
    Color[] colors = new Color[6];
    Point startPoint = new Point(0,0);
    double[][] pointPositions = new double[client.points.length][2];
    int closestZIndex = 0 ;
    face[] faces = {new face(),new face(),new face()};

    public viewport(){
        setBackground(Color.black);
        colors[0] = Color.red;
        colors[1] = Color.blue;
        colors[2] = Color.yellow;
        colors[3] = Color.white;
        colors[4] = Color.green;
        colors[5] = Color.ORANGE;
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
                    int scale = (int)(1.5 * Math.min(getWidth()/2, getHeight()/2));

                    double[][] rotationX =
                    {
                        {1,0,0},
                        {0,(float)Math.cos(xangle),(float)-Math.sin(xangle)},
                        {0,(float)Math.sin(xangle),(float)Math.cos(xangle)}
                    };
                    double[][] rotationY =
                    {
                        {(float)Math.cos(yangle),0,(float)Math.sin(yangle)},
                        {0,1,0},
                        {(float)-Math.sin(yangle),0, (float)Math.cos(yangle)}
                    };
                    double[][] rotationZ =
                    {
                        {(float)Math.cos(zangle),(float)-Math.sin(zangle),0},
                        {(float)Math.sin(zangle),(float)Math.cos(zangle),0},
                        {0,0,1}
                    };
                    
                    double c = Math.cos(angle);
                    double s = Math.sin(angle);
                    double C = 1 -c;
                    double[][] rotationAll =
                    {
                        {x*x*C+c,x*y*C-z*s,x*z*C +y*s},
                        {y*x*C+z*s,y*y*C+c,y*z*C-x*s},
                        {z*x*C-y*s,z*y*C+x*s,z*z*C+c}
                    };
                    
                    double closestZ = Integer.MIN_VALUE;
                    for (int i =0; i<client.points.length; i++) {
                        double[][] rotated = client.matrixMultiply(rotationAll, client.points[i].toMatrix());
                        //rotated = client.matrixMultiply(rotationY, rotated);
                        //rotated = client.matrixMultiply(rotationZ, rotated);
                        
                        client.pointsRotated[i] = new vector3(rotated[0][0], rotated[1][0], rotated[2][0]);
                        
                        double distance = 2;
                        double depth = 1/ (distance- rotated[2][0]);
                        double[][] projection2 =
                        {
                            {depth,0,0},
                            {0,depth,0}
                        };
                        double[][] pos = client.matrixMultiply(projection2, rotated);
                        pointPositions[i][0] = pos[0][0] *scale + getWidth()/2;
                        pointPositions[i][1] = pos[1][0] *scale + getHeight()/2;

                        if(rotated[2][0] > closestZ)
                        {
                            closestZ = rotated[2][0];
                            closestZIndex = i;
                        }
                    } 
                        
                    repaint();

                    double mouseX = evt.getPoint().x - startPoint.x;
                    double mouseY = evt.getPoint().y - startPoint.y;

                    double magnitude = Math.sqrt(mouseX * mouseX + mouseY * mouseY);
                    double normalizedX = mouseX / magnitude;
                    double normalizedY = mouseY / magnitude;

                    // Create the axis vector
                    x = normalizedY;
                    y = normalizedX; 
                    z = 0;
                    angle +=0.1;
                    startPoint = evt.getPoint();
                }
                
            }
        });
    }
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        
        g2.setColor(Color.BLUE);
        g2.fillOval((int)(pointPositions[closestZIndex][0]),(int)(pointPositions[closestZIndex][1]), 10, 10);
        g2.setColor(Color.green);

        int faceIndex = 0;
        for(int i = 0; i<client.points.length; i++)
        {
            boolean x = client.points[closestZIndex].getX() == client.points[i].getX();
            boolean y = client.points[closestZIndex].getY() == client.points[i].getY();
            boolean z = client.points[closestZIndex].getZ() == client.points[i].getZ();
            if((!x && y && z) || (x && !y && z) || (x && y && !z))
            {
                g2.fillOval((int)(pointPositions[i][0]),(int)(pointPositions[i][1]), 10, 10);
                faces[faceIndex].addPoint((int)(pointPositions[closestZIndex][0]),(int)(pointPositions[closestZIndex][1]),closestZIndex);
                faces[faceIndex].addPoint((int)(pointPositions[i][0]),(int)(pointPositions[i][1]),i);
                for(int j = 0; j<client.points.length; j++)
                {
                    if(checkAdjacency(x, y, z, i, j))
                    {
                        faces[faceIndex].addPoint((int)pointPositions[j][0],(int)pointPositions[j][1],j); 
                    }
                }
                for(int j = 0; j<client.points.length; j++)
                {
                    if(checkAdjacency(x, y, z, closestZIndex, j))
                    {
                        faces[faceIndex].addPoint((int)pointPositions[j][0],(int)pointPositions[j][1],j); 
                    }
                }
                faceIndex++;
            }
        }

        //this is really bad. figure out a better way to draw the sides, so that they don't draw in the wrong order

        double zero  = faces[0].getAvgPointPos();
        double one = faces[1].getAvgPointPos();
        double two = faces[2].getAvgPointPos();
        double biggestAvg = Math.max(zero, Math.max(two,one));
        double smallestAvg = Math.min(zero, Math.min(two,one));
        for(int j =0; j<faces.length; j++)
        {
            if(faces[j].getAvgPointPos() == smallestAvg)
            { 
                g2.setColor(Color.cyan);
                g2.setStroke(new BasicStroke(1));
                g2.fillPolygon(faces[j]);
                g2.setStroke(new BasicStroke(4));
                g2.setColor(Color.black);
                g2.drawPolygon(faces[j]);
                break;
            }
        }
        for(int j =0; j<faces.length; j++)
        {
            if(faces[j].getAvgPointPos() != smallestAvg&&faces[j].getAvgPointPos() != biggestAvg)
            {
                g2.setColor(Color.yellow);
                g2.setStroke(new BasicStroke(1));
                g2.fillPolygon(faces[j]);
                g2.setStroke(new BasicStroke(4));
                g2.setColor(Color.black);
                g2.drawPolygon(faces[j]);
                break;
            }
        }
        for(int j =0; j<faces.length; j++)
        {
            if(faces[j].getAvgPointPos() == biggestAvg)
            {
                g2.setColor(Color.red);
                g2.setStroke(new BasicStroke(1));
                g2.fillPolygon(faces[j]);
                g2.setStroke(new BasicStroke(4));
                g2.setColor(Color.black);
                g2.drawPolygon(faces[j]);
            }
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
