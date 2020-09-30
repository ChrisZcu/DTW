import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.providers.MapBox;
import de.fhpotsdam.unfolding.utils.MapUtils;
import processing.core.PApplet;

public class UserInterface extends PApplet {
    UnfoldingMap map;

    @Override
    public void settings(){
        size(1000,800,P2D);
    }

    @Override
    public void setup(){
        String WHITE_MAP_PATH = "https://api.mapbox.com/styles/v1/pacemaker-yc/ck4gqnid305z61cp1dtvmqh5y/tiles/512/{z}/{x}/{y}@2x?access_token=pk.eyJ1IjoicGFjZW1ha2VyLXljIiwiYSI6ImNrNGdxazl1aTBsNDAzZW41MDhldmQyancifQ.WPAckWszPCEHWlyNmJfY0A";

        map = new UnfoldingMap(this, new MapBox.CustomMapBoxProvider(WHITE_MAP_PATH));
        map.setZoomRange(1, 20);
        map.setBackgroundColor(255);
        MapUtils.createDefaultEventDispatcher(this, map);
        map.zoomAndPanTo(13, new Location(41.14, -8.639));


    }
    public static void main(String[] args) {
        PApplet.main(new String[]{UserInterface.class.getName()});
    }
}

