import java.lang.reflect.Array;
import java.util.ArrayList;

public class Cluster implements Cluster_Interface {

    private Image centroid;
    private ArrayList<Image> cluster;
    private int user_classified_digit;

    public Cluster() {
        this.cluster = new ArrayList<>();
    }

    public void add_image_to_cluster(Image image) {
        cluster.add(image);
    }

    public ArrayList<Image> get_cluster() {
        return cluster;
    }

    private Image calculate_centroid() {
        //TODO: Calculate the centroid based on all the images in the cluster
        /*
        * returns the image that is the average of all of the images in the cluster
        * */
        return null;
    }

    public Image centroid() {
        return calculate_centroid();
    }

    public void label_digits() {
        user_classified_digit = centroid.label();
        for (Image image : cluster) {
            image.label(user_classified_digit);
        }
    }
}
