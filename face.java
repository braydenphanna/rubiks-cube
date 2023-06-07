import java.awt.*;
import java.util.*;

public class face extends Polygon{
    private ArrayList<Integer> ptKeys = new ArrayList<Integer>();
    private Color color;
    public face(Color c)
    {
        color = c;
    }
    public void addPointKey(int j)
    {
        ptKeys.add(j);
    }
    public Color getColor()
    {
        return color;
    }
    public void setIndex(Color c)
    {
        color = c;
    }
    public void update()
    {
        for(int i = 0; i<ptKeys.size(); i++)
        {
            addPoint((int)viewport.pointPositions[ptKeys.get(i)][0], (int)viewport.pointPositions[ptKeys.get(i)][1]);
        }
    }
    public ArrayList<Integer> getKeys()
    {
        return ptKeys;
    }
    public double getAvgPointPos()
    {
        if(ptKeys.size()==0) return 0;
        else {
        double total = 0;
        for(int i = 0; i<ptKeys.size(); i++)
        {
            total += client.pointsRotated[ptKeys.get(i)].getZ();
        }
        total/=(double)ptKeys.size();
        return total;
        }
    }
    @Override
    public void reset()
    {
        super.reset();
    }
}
