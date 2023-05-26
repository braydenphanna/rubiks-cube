import java.awt.*;
import javax.swing.*;
import java.util.*;

public class face extends Polygon{
    private ArrayList<Double> pts = new ArrayList<Double>();

    public void addPoint(int x, int y, int j)
    {
        super.addPoint(x,y);
        pts.add(client.pointsRotated[j].getZ());
    }
    public double getAvgPointPos()
    {
        if(pts.size()==0) return 0;
        else {
        double total = 0;
        for(int i = 0; i<pts.size(); i++)
        {
            total += pts.get(i);
        }
        total/=(double)pts.size();
        return total;
        }
    }
    @Override
    public void reset()
    {
        super.reset();
        pts.clear();
    }
}
