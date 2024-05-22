import java.lang.reflect.Array;
import java.util.ArrayList;

public class Cluster implements Cluster_Interface {


    private Image centroid;
    private ArrayList<Image> cluster;
    private int user_classified_digit;

    public Cluster(){}



    public void add_image_to_cluster(Image image){
        cluster.add(image);
    }

    public ArrayList<Image> get_cluster(){
        return cluster;
    }

    private Image calculate_centroid(){
        //TODO, Calculate the centroid based on all the images in the cluster
        return null;
    }

    public Image centroid(){
        return calculate_centroid();
    }

    @Override
    public int assignDigit(int digit) {
        return 0;
    }
}
