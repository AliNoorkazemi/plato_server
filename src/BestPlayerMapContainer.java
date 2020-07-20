import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BestPlayerMapContainer implements Serializable {

    private Map<String,Integer> name_score;
    private Map<String,String> name_image;

    public Map<String, Integer> getName_score() {
        if ( name_score==null)
            name_score = new ConcurrentHashMap<>();
        return name_score;
    }

    public void setName_score(Map<String, Integer> name_score) {
        this.name_score = name_score;
    }

    public Map<String, String> getName_image() {
        if ( name_image == null)
            name_image = new ConcurrentHashMap<>();
        return name_image;
    }


    public void setName_image(Map<String, String> name_image) {
        this.name_image = name_image;
    }

}