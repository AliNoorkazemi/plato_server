import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BestPlayerMapContainer {

    private Map<String,Integer> name_score;
    private Map<String,byte[]> name_image;
    private Map<String,Integer> name_ranked;

    public Map<String, Integer> getName_score() {
        if ( name_score==null)
            name_score = new ConcurrentHashMap<>();
        return name_score;
    }

    public void setName_score(Map<String, Integer> name_score) {
        this.name_score = name_score;
    }

    public Map<String, byte[]> getName_image() {
        if ( name_image == null)
            name_image = new ConcurrentHashMap<>();
        return name_image;
    }

    public void setName_image(Map<String, byte[]> name_image) {
        this.name_image = name_image;
    }

    public Map<String, Integer> getName_ranked() {
        if ( name_ranked==null)
            name_ranked = new ConcurrentHashMap<>();
        return name_ranked;
    }

    public void setName_ranked(Map<String, Integer> name_ranked) {
        this.name_ranked = name_ranked;
    }
}
