package all.common;

import java.awt.*;

public class FramePosition {
    public static void toCenter(Component comp){
        GraphicsEnvironment ge=GraphicsEnvironment.getLocalGraphicsEnvironment();
        Rectangle rec=ge.getDefaultScreenDevice().getDefaultConfiguration().getBounds();
        comp.setLocation(((int)rec.getWidth()-comp.getWidth())/2,
                ((int)rec.getHeight()-comp.getHeight())/2);
    }
}
