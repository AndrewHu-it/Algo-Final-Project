import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.lang.Math;


public class K_Means {

    /*
    * /*=============<<< PROGRAM PARAMETERS >>>==============

            DISTANCE METRICS:
            -hamming
            -manhattan
            -euclidean
            -cosine
            -l3

            MAX ITERATIONS OF K MEANS:
            -steps <int>

            NUMBER OF CATEGORIES (K):
            -k <int>

            CENTROID INITIALIZATION METHOD:
            -k_random
            -k_different

            DYNAMICALLY INCREASE CLUSTER COUNT:
            -dynamic

            SYSTEM UPDATES DURING RUN:
            -updates

         ======================================================*/








    public enum DistanceType {HAMMING, MANHATTAN, EUCLIDEAN, COSINE, L3 }
    public enum Centroid_Type {K_RANDOM, K_DIFFERENT}



    public static ArrayList<Cluster> clusters = new ArrayList<>();
    public static DistanceType distance = DistanceType.EUCLIDEAN;
    public static Centroid_Type centroid_type = Centroid_Type.K_RANDOM;
    public static int k = 10;
    public static boolean dynamic = false;
    public static boolean updates = false;



    public static final int TEST_SEED = 100;
    public static Random random = new Random(TEST_SEED);
    public static double random() { return random.nextDouble(); } // In range [0.0, 1.0)


    public static int reclassify(Image[] images){
        int num_swaps = 0;

        ArrayList<ArrayList<Image>> new_image_clusters = new ArrayList<>(k);
        for (int i = 0; i < k; i++){
            new_image_clusters.add(new ArrayList<>());
        }

        //go through each of the clusters
        for (int i = 0; i < clusters.size(); i ++){


            Cluster current_cluster = clusters.get(i);

            //Go through each of the images in the cluster
            for (int j = 0; j <current_cluster.get_cluster().size(); j++){
                Image current_image = current_cluster.get_cluster().get(j);

                int index_of_min_cluster = i;
                double min_distance = Double.MAX_VALUE;


                for (int l = 0; l < k; l++){
                    Image current_centroid = clusters.get(l).centroid();
                    double distance = calculate_distance(current_image, current_centroid);
                    if (distance < min_distance){
                        min_distance = distance;
                        index_of_min_cluster = l;
                    }
                }

                //TODO: I think that right here is where we would add a new cluster if all of the distances are too far away.
                /*
                 * Maybe it can return a certain number (-1?) if it creates a new cluster (obviously that means that it is not done)
                 * As soon as we decide to make another cluster, we may want to just add it to the global clusters array with the centroid as the one image
                 *  --This way if we have other images we can add them to this bucket if they are similar
                 *  -- We may also want to make a cap on the number of new clusters that can be created (20 or so)
                 * */


                new_image_clusters.get(index_of_min_cluster).add(current_image);

                if (index_of_min_cluster != i){
                    num_swaps++;
                }

            }

            if (updates){
                System.out.println("Images from cluster " + i + "  reclassified");
            }

        }

        for (int i = 0; i < new_image_clusters.size(); i ++){
            if (i < clusters.size()){
                clusters.get(i).set_image_cluster(new_image_clusters.get(i));
            }
            //TODO: If i esceeds the number of already existing clusters, than we know that we have to make new cluster
        }

        return num_swaps;
    }

    public static void k_means(Image[] images, int max_steps){
        initialize_centroids(images);
        if (updates){
            System.out.println("Centroid Initialization has just been completed");
        }

        initially_categorize_images(images);
        if (updates){
            System.out.println("All images successfully initially categorized. \n Now begin K-Means process");
        }

        int num_updates = 0;
        int num_cycles = 0;
        do {
            if(updates){
                System.out.println("===========ITERATION " + num_cycles + " ============");
            }

            num_updates = reclassify(images);
            num_cycles++;
            System.out.println(num_updates + " images changed");

        } while (num_updates > 0 && (num_cycles < max_steps));
        if (updates){
            System.out.println("We itereated k means " + num_cycles + " times");
        }
    }


    public static double calculate_distance(Image image_1, Image image_2){
        switch (distance) {
            case HAMMING:
                return Hamming_Distance(image_1, image_2);
            case MANHATTAN:
                return Manhattan_Distance(image_1, image_2);
            case EUCLIDEAN:
                return Euclidean_Distance(image_1, image_2);
            case COSINE:
                return Cosine_Distance(image_1, image_2);
            case L3:
                return L3_Distance(image_1, image_2);
            default:
                throw new IllegalArgumentException("Unknown distance type: " + distance);
        }
    }



    public static void initialize_centroids(Image[] images){
        switch(centroid_type){
            case K_RANDOM: KR_centroid_intialization(images); break;
            case K_DIFFERENT: KD_centroid_intialization(images); break;
        }
    }



    public static void initially_categorize_images(Image[] images){
        for (int i = 0; i < images.length; i++){

            Image current_image = images[i];
            double smallest_centroid_distance = Double.MAX_VALUE;
            int index_of_centroid = -1;
            for (int j = 0; j < clusters.size(); j ++){
                Cluster current_cluster = clusters.get(j);
                double distance = calculate_distance(current_image, current_cluster.non_updated_centroid());
                if (distance < smallest_centroid_distance){
                    smallest_centroid_distance = distance;
                    index_of_centroid = j;
                }
            }
            clusters.get(index_of_centroid).add_image_to_cluster(current_image);
        }

    }



    public static void user_classify(){
        Image[] centroids = new Image[10];
        for (int i = 0; i < clusters.size(); i++){
            centroids[i] = clusters.get(i).centroid();
        }
        Viewer.Attributes attributes = new Viewer.Attributes();
        attributes.showClassify(true);

        Viewer.invoke(centroids, "User Classification", attributes);
        for (Cluster cluster : clusters){
            cluster.label_digits();
        }

    }

    public static double percentage_accuracy(Image[] images, String filename){
        int[] answers = new int[0];
        try{
            answers = Image.readLabels(filename);
        } catch (Exception e){
            System.out.println("Invalid filename: " + filename);
        }
        int numCorrect = 0;
        int total = images.length;
        for (int i = 0; i < images.length; i ++){
            if (answers[i] == images[i].label()){
                numCorrect++;
            }
        }
        return numCorrect/(double)total;
    }



    public static void main(String[] args) throws IOException {
        int max_steps = 100;

        for (int i = 0; i < args.length; i ++){
            switch(args[i].toUpperCase()){
                case "-HAMMING":
                    distance = DistanceType.HAMMING;
                    break;
                case "-MANHATTAN":
                    distance = DistanceType.MANHATTAN;
                    break;
                case "-EUCLIDEAN":
                    distance = DistanceType.EUCLIDEAN;
                    break;
                case "-COSINE":
                    distance = DistanceType.COSINE;
                    break;
                case "-L3":
                    distance = DistanceType.L3;
                    break;
                case "-STEPS":
                    max_steps = Integer.parseInt(args[++i]);
                    break;
                case "-K":
                    k = Integer.parseInt(args[++i]);
                    break;
                case "-K_RANDOM":
                    centroid_type = Centroid_Type.K_RANDOM;
                    break;
                case "-K_DIFFERENT":
                    centroid_type = Centroid_Type.K_DIFFERENT;
                    break;
                case "-DYNAMIC":
                    dynamic = true;
                    break;
                case "-UPDATES":
                    updates = true;
                    break;
                default:
                    System.out.println("Unknown parameter: " + args[i]);
            }

        }


        for (int i = 0; i < k; i++) {

            clusters.add(new Cluster());
        }

        //Read the images
        Image[] images = Image.readImages("train-images");


        //Works, but very poorly
        k_means(images, max_steps);


        //User labels
        user_classify();


        double accuracy = percentage_accuracy(images, "train-labels");
        System.out.println(accuracy);


        //TODO: File output:


    }








    //--------------===<<DISTANCE METHODS>>===-----------------
    private static double L1_Distance(Image image_1, Image image_2){
        double distance = 0;
        for (int row = 0; row < image_1.rows(); row++) {
            for (int col = 0; col < image_1.columns(); col++) {
                distance += Math.abs(image_1.get(row, col) - image_2.get(row, col));
            }
        }
        return distance;
    }

    private static double L3_Distance(Image image_1, Image image_2){
        double distance = 0;
        for (int row = 0; row < image_1.rows(); row++) {
            for (int col = 0; col < image_1.columns(); col++) {
                distance += Math.pow(Math.abs(image_1.get(row, col) - image_2.get(row, col)), 3);
            }
        }
        return distance;
    }

    private static double Cosine_Distance(Image image_1, Image image_2){
        double dotProduct = 0;
        double normA = 0;
        double normB = 0;
        for (int row = 0; row < image_1.rows(); row++) {
            for (int col = 0; col < image_1.columns(); col++) {
                int pixelA = image_1.get(row, col);
                int pixelB = image_2.get(row, col);
                dotProduct += pixelA * pixelB;
                normA += Math.pow(pixelA, 2);
                normB += Math.pow(pixelB, 2);
            }
        }
        return 1 - dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }

    private static double Euclidean_Distance(Image image_1, Image image_2){
        double distance = 0;
        for (int row = 0; row < image_1.rows(); row++) {
            for (int col = 0; col < image_1.columns(); col++) {
                distance += Math.pow(image_1.get(row, col) - image_2.get(row, col), 2);
            }
        }
        return Math.sqrt(distance);
    }

    private static double Manhattan_Distance(Image image_1, Image image_2){
        double distance = 0;
        for (int row = 0; row < image_1.rows(); row++) {
            for (int col = 0; col < image_1.columns(); col++) {
                distance += Math.abs(image_1.get(row, col) - image_2.get(row, col));
            }
        }
        return distance;
    }

    private static double Hamming_Distance(Image image_1, Image image_2) {
        double distance = 0;
        for (int row = 0; row < image_1.rows(); row++) {
            for (int col = 0; col < image_1.columns(); col++) {
                if (image_1.get(row, col) != image_2.get(row, col)) {
                    distance++;
                }
            }
        }
        return distance;
    }


    //-------------======<<Centroid Intialization>>====----------

    private static void KR_centroid_intialization(Image[] images) {
        for (int i = 0; i < k; i++) {
            int randomIndex = (int) (random() * images.length);
            Image random_image = images[randomIndex];
            clusters.get(i).set_centroid(random_image);
        }
    }





    private static void KD_centroid_intialization(Image[] images) {
        //TODO:
        /*
        * We need to figure out some good method that will make the centroids as different as possible to start with.
        * */


    }


}
