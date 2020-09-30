import de.fhpotsdam.unfolding.UnfoldingMap;
import processing.core.PApplet;

public class UserInterface extends PApplet {
    UnfoldingMap map;

    @Override
    public void settings(){

        size(1000,800,P2D);
    }

    public static void main(String[] args) {
        PApplet.main(new String[]{UserInterface.class.getName()});
    }
}

