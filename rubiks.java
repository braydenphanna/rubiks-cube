import java.awt.*;

public class rubiks
{
    public static Color[][][] rubiks = new Color[6][3][3];
    public static Color[][][] twoxtwo = new Color[6][2][2];
    public rubiks()
    {
        rubiks = new Color[][][]{{{Color.RED,Color.RED,Color.RED},
                                 {Color.RED,Color.RED,Color.RED},
                                 {Color.RED,Color.BLUE,Color.RED}},
                                {{Color.BLUE,Color.BLUE,Color.BLUE},
                                 {Color.BLUE,Color.BLUE,Color.BLUE},
                                 {Color.BLUE,Color.BLUE,Color.BLUE}},
                                {{Color.WHITE,Color.WHITE,Color.WHITE},
                                 {Color.WHITE,Color.WHITE,Color.WHITE},
                                 {Color.WHITE,Color.WHITE,Color.WHITE}},
                                {{Color.YELLOW,Color.YELLOW,Color.YELLOW},
                                 {Color.YELLOW,Color.YELLOW,Color.YELLOW},
                                 {Color.white,Color.YELLOW,Color.YELLOW}},
                                {{Color.GREEN,Color.GREEN,Color.GREEN},
                                 {Color.GREEN,Color.GREEN,Color.GREEN},
                                 {Color.GREEN,Color.GREEN,Color.GREEN}},
                                {{Color.ORANGE,Color.ORANGE,Color.ORANGE},
                                 {Color.ORANGE,Color.ORANGE,Color.ORANGE},
                                 {Color.ORANGE,Color.ORANGE,Color.ORANGE}}};
                                 
        twoxtwo = new Color[][][]{{{Color.red, Color.red},
                                   {Color.red, Color.red}},
                                {{Color.blue, Color.blue},
                                   {Color.blue, Color.blue}},
                                {{Color.white, Color.white},
                                   {Color.white, Color.white}},
                                {{Color.yellow, Color.yellow},
                                   {Color.yellow, Color.yellow}},
                                {{Color.green, Color.green},
                                   {Color.green, Color.green}},
                                {{Color.orange, Color.orange},
                                   {Color.orange, Color.orange}}};
    }
    public Color[][][] getRubiks()
    {
        return rubiks;
    }
    public static void rotateFaceClockwise(int faceIndex)
    {
    }
}
