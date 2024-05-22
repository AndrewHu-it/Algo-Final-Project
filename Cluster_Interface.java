import java.util.ArrayList;

public interface Cluster_Interface {


    /**
     * Assigns the digit to all of the images in the cluster after user classifies .
     */
    void label_digits();

    Image centroid();

    ArrayList<Image> get_cluster();

    void add_image_to_cluster(Image image);


}
