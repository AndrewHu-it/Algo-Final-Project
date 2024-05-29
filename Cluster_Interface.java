import java.util.ArrayList;


public interface Cluster_Interface {

    /**
     * Assigns the digit to all of the images in the cluster after user classifies.
     */
    void label_digits();

    /**
     * Returns the current centroid of the cluster.
     *
     * @return the centroid image.
     */
    Image centroid();

    /**
     * Returns the list of images in the cluster.
     *
     * @return the list of images.
     */
    ArrayList<Image> get_cluster();

    /**
     * Adds an image to the cluster and marks the centroid as needing an update.
     *
     * @param image the image to add.
     */
    void add_image_to_cluster(Image image);

    /**
     * Sets the centroid of the cluster.
     *
     * @param image the image to set as the centroid.
     */
    void set_centroid(Image image);

    /**
     * Returns the digit classified by the user.
     *
     * @return the user classified digit.
     */
    int getUser_classified_digit();
}
