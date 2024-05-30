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
            -k_pp

            SYSTEM UPDATES DURING RUN:
            -updates

         ======================================================*/


    public enum DistanceType {HAMMING, MANHATTAN, EUCLIDEAN, COSINE, L3}

    public enum Centroid_Type {K_RANDOM, K_DIFFERENT, K_PP}


    public static ArrayList<Cluster> clusters = new ArrayList<>();
    public static DistanceType distance = DistanceType.EUCLIDEAN;
    public static Centroid_Type centroid_type = Centroid_Type.K_RANDOM;
    public static int k = 10;
    public static boolean updates = false;


    public static final int TEST_SEED = 3;
    public static Random random = new Random();

    public static double random() {
        return random.nextDouble();
    } // In range [0.0, 1.0)


    public static int reclassify(Image[] images) {
        int num_swaps = 0;

        ArrayList<ArrayList<Image>> new_image_clusters = new ArrayList<>(k);
        for (int i = 0; i < k; i++) {
            new_image_clusters.add(new ArrayList<>());
        }

        //go through each of the clusters
        for (int i = 0; i < clusters.size(); i++) {


            Cluster current_cluster = clusters.get(i);

            //Go through each of the images in the cluster
            for (int j = 0; j < current_cluster.get_cluster().size(); j++) {
                Image current_image = current_cluster.get_cluster().get(j);

                int index_of_min_cluster = i;
                double min_distance = Double.MAX_VALUE;


                for (int l = 0; l < k; l++) {
                    Image current_centroid = clusters.get(l).centroid();
                    double distance = calculate_distance(current_image, current_centroid);
                    if (distance < min_distance) {
                        min_distance = distance;
                        index_of_min_cluster = l;
                    }
                }

                new_image_clusters.get(index_of_min_cluster).add(current_image);

                if (index_of_min_cluster != i) {
                    num_swaps++;
                }

            }

        }

        for (int i = 0; i < new_image_clusters.size(); i++) {
            if (i < clusters.size()) {
                clusters.get(i).set_image_cluster(new_image_clusters.get(i));
            }
        }

        return num_swaps;
    }

    public static void k_means(Image[] images, int max_steps) {
        initialize_centroids(images);


        if (updates) {
            System.out.println("Centroid Initialization has just been completed");
        }

        initially_categorize_images(images);
        if (updates) {
            System.out.println("All images successfully initially categorized. \n Now begin K-Means process");
        }

        int num_updates = 0;
        int num_cycles = 0;
        do {
            if (updates) {
                System.out.println("===========ITERATION " + num_cycles + " ============");
            }

            num_updates = reclassify(images);
            num_cycles++;
            if (updates) {
                System.out.println(num_updates + " images changed");
            }

        } while (num_updates > 2 && (num_cycles < max_steps));
        if (updates) {
            System.out.println("We itereated k means " + num_cycles + " times");
        }
    }


    public static double calculate_distance(Image image_1, Image image_2) {
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


    public static void initialize_centroids(Image[] images) {
        switch (centroid_type) {
            case K_RANDOM:
                KR_centroid_intialization(images);
                break;
            case K_DIFFERENT:
                KD_centroid_initialization(images);
                break;
            case K_PP:
                K_PP_centroid_initialization(images);
                break;
        }
    }


    public static void initially_categorize_images(Image[] images) {
        for (int i = 0; i < images.length; i++) {

            Image current_image = images[i];
            double smallest_centroid_distance = Double.MAX_VALUE;
            int index_of_centroid = -1;
            for (int j = 0; j < clusters.size(); j++) {
                Cluster current_cluster = clusters.get(j);
                double distance = calculate_distance(current_image, current_cluster.non_updated_centroid());
                if (distance < smallest_centroid_distance) {
                    smallest_centroid_distance = distance;
                    index_of_centroid = j;
                }
            }
            clusters.get(index_of_centroid).add_image_to_cluster(current_image);
        }

    }


    public static void user_classify() {
        Image[] centroids = new Image[clusters.size()];
        for (int i = 0; i < clusters.size(); i++) {
            centroids[i] = clusters.get(i).centroid();
        }
        Viewer.Attributes attributes = new Viewer.Attributes();
        attributes.showClassify(true);

        Viewer.invoke(centroids, "User Classification", attributes);
        for (Cluster cluster : clusters) {
            cluster.label_digits();
        }

    }

    public static double percentage_accuracy(Image[] images, String filename, ArrayList<Image> incorrect_images) {
        int[] answers = new int[0];
        try {
            answers = Image.readLabels(filename);
        } catch (Exception e) {
            System.out.println("Invalid filename: " + filename);
        }
        int numCorrect = 0;
        int total = images.length;
        for (int i = 0; i < images.length; i++) {
            if (answers[i] == images[i].label()) {
                numCorrect++;
            } else {
                incorrect_images.add(images[i]);
            }
        }
        return numCorrect / (double) total;
    }


    public static void main(String[] args) throws IOException {
        int max_steps = 100;

        for (int i = 0; i < args.length; i++) {
            switch (args[i].toUpperCase()) {
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
                    if (max_steps == -1) {
                        max_steps = 1000;
                    }
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
                case "-K_PP":
                    centroid_type = Centroid_Type.K_PP;
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
        images = optimize_images(images);

        k_means(images, max_steps);

        //User classify
        user_classify();

        //-------All the training is done -------
        ArrayList<Image> missed_images_AL = new ArrayList<>();
        double accuracy = percentage_accuracy(images, "train-labels", missed_images_AL);
        System.out.println(accuracy);

        Image[] missed_images_array = missed_images_AL.toArray(new Image[0]);
        Image[] centroids = new Image[k];
        for (int i = 0; i < clusters.size(); i++) {
            Image centroid = clusters.get(i).centroid();
            centroids[i] = centroid;
        }


        Image.writeImages(centroids, "centroid-images");
        Image.writeLabels(centroids, "centroid-labels");


        Image.writeLabels(missed_images_array, "incorrect-labels");
        Image.writeImages(missed_images_array, "incorrect-images");

        Image.writeImages(missed_images_array, "incorrect-images");
        Image.writeLabels(missed_images_array, "incorrect-labels");


        for (int i = 0; i < k; i++) {
            String cluster_name = "cluster" + i + "-images";
            Cluster current = clusters.get(i);
            Image[] cluster = current.get_cluster().toArray(new Image[0]);
            Image.writeImages(cluster, cluster_name);
        }


    }


    //--------------===<<DISTANCE METHODS>>===-----------------
    private static double L1_Distance(Image image_1, Image image_2) {
        double distance = 0;
        for (int row = 0; row < image_1.rows(); row++) {
            for (int col = 0; col < image_1.columns(); col++) {
                distance += Math.abs(image_1.get(row, col) - image_2.get(row, col));
            }
        }
        return distance;
    }

    private static double L3_Distance(Image image_1, Image image_2) {
        double distance = 0;
        for (int row = 0; row < image_1.rows(); row++) {
            for (int col = 0; col < image_1.columns(); col++) {
                distance += Math.pow(Math.abs(image_1.get(row, col) - image_2.get(row, col)), 3);
            }
        }
        return distance;
    }

    private static double Cosine_Distance(Image image_1, Image image_2) {
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

    private static double Euclidean_Distance(Image image_1, Image image_2) {
        double distance = 0;
        for (int row = 0; row < image_1.rows(); row++) {
            for (int col = 0; col < image_1.columns(); col++) {
                distance += Math.pow(image_1.get(row, col) - image_2.get(row, col), 2);
            }
        }
        return Math.sqrt(distance);
    }

    private static double Manhattan_Distance(Image image_1, Image image_2) {
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

    public static void K_PP_centroid_initialization(Image[] images) {
        ArrayList<Integer> chosenNumbers = new ArrayList<>();
        Image[] centroids = new Image[k];

        //First we have to choose a center at random.
        int randomIndex = random.nextInt(images.length);
        chosenNumbers.add(randomIndex);
        centroids[0] = images[randomIndex];

        //distances
        double[] distances = new double[images.length];
        for (int i = 0; i < images.length; i++) {
            distances[i] = Double.MAX_VALUE;
        }
        distances[randomIndex] = 0;

        //loop for other centroids
        for (int clusterNum = 1; clusterNum < k; clusterNum++) {
            double totalDistance = 0;

            //d(x) for all images
            for (int i = 0; i < images.length; i++) {
                if (!chosenNumbers.contains(i)) {
                    double minDistance = Double.MAX_VALUE;

                    //find the minimum distance to all of the already made centroids
                    for (int chosenIndex : chosenNumbers) {
                        double distance = calculate_distance(images[i], images[chosenIndex]);
                        if (distance < minDistance) {
                            minDistance = distance;
                        }
                    }
                    distances[i] = minDistance * minDistance;
                    totalDistance += distances[i];
                }
            }

            // probability distrubution, and select some area under the curve --> cumulative probability distribution x (integral of PDF)
            double[] cumulativeProbabilities = new double[images.length];
            cumulativeProbabilities[0] = distances[0] / totalDistance;
            for (int i = 1; i < images.length; i++) {
                cumulativeProbabilities[i] = cumulativeProbabilities[i - 1] + (distances[i] / totalDistance);
            }

            //based on the distribution, it selects the next centroid
            double randomValue = random.nextDouble();
            for (int i = 0; i < images.length; i++) {
                if (randomValue < cumulativeProbabilities[i] && !chosenNumbers.contains(i)) {
                    chosenNumbers.add(i);
                    centroids[clusterNum] = images[i];
                    break;
                }
            }
        }

        clusters.clear();
        for (Image centroid : centroids) {
            Cluster c = new Cluster(centroid);
            clusters.add(c);
        }
    }

    private static void KD_centroid_initialization(Image[] images) {
        ArrayList<Image> differentCentroids = new ArrayList<>();

        //random centroid
        differentCentroids.add(images[random.nextInt(images.length)]);

        //choose centroids until we have k centroids
        while (differentCentroids.size() < k) {
            Image furthestImage = null;
            double maxMinDistance = Double.MIN_VALUE;

            for (Image candidate : images) {
                //skip if the current one is already a centroid
                if (differentCentroids.contains(candidate)) continue;

                //minimum distance from this one to any chosen centroid
                double minDistance = Double.MAX_VALUE;
                for (Image centroid : differentCentroids) {
                    double distance = calculate_distance(candidate, centroid);
                    if (distance < minDistance) {
                        minDistance = distance;
                    }
                }

                //choose the one that has the maximum of these minimum distances
                if (minDistance > maxMinDistance) {
                    maxMinDistance = minDistance;
                    furthestImage = candidate;
                }
            }

            //add the chosen one to the list of centroids
            differentCentroids.add(furthestImage);
        }

        //initialize clusters with the chosen centroids
        clusters.clear();
        for (Image centroid : differentCentroids) {
            Cluster c = new Cluster(centroid);
            clusters.add(c);
        }
    }


    public static Image[] optimize_images(Image[] images) {
        for (int i = 0; i < images.length; i++) {
            images[i] = normalizeImage(images[i]);
            images[i] = applyGaussianBlur(images[i]);

        }
        return images;
    }

    private static Image normalizeImage(Image image) {
        int rows = image.rows();
        int columns = image.columns();
        byte[][] normalizedPixels = new byte[rows][columns];

        double sum = 0;
        double sumSq = 0;
        int totalPixels = rows * columns;

        // Calculate the sum and sum of squares of all pixel values
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                int pixel = image.get(row, col);
                sum += pixel;
                sumSq += pixel * pixel;
            }
        }

        // Calculate mean and standard deviation
        double mean = sum / totalPixels;
        double variance = (sumSq / totalPixels) - (mean * mean);
        double stdDev = Math.sqrt(variance);

        // Normalize pixel values
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                int pixel = image.get(row, col);
                double normalizedValue = (pixel - mean) / stdDev;
                // Scale to 0-255 and clamp
                int scaledValue = (int) Math.round(normalizedValue * 128 + 128);
                scaledValue = Math.min(255, Math.max(0, scaledValue));
                normalizedPixels[row][col] = (byte) scaledValue;
            }
        }

        return new Image(normalizedPixels, image.label());
    }


    private static Image applyGaussianBlur(Image image) {
        int rows = image.rows();
        int columns = image.columns();
        byte[][] blurredPixels = new byte[rows][columns];

        double[][] kernel = {
                {1, 4, 7, 4, 1},
                {4, 16, 26, 16, 4},
                {7, 26, 41, 26, 7},
                {4, 16, 26, 16, 4},
                {1, 4, 7, 4, 1}
        };

        double kernelSum = 273; // Sum of all elements in the kernel

        for (int row = 2; row < rows - 2; row++) {
            for (int col = 2; col < columns - 2; col++) {
                double pixelSum = 0;
                for (int kRow = -2; kRow <= 2; kRow++) {
                    for (int kCol = -2; kCol <= 2; kCol++) {
                        pixelSum += image.get(row + kRow, col + kCol) * kernel[kRow + 2][kCol + 2];
                    }
                }
                blurredPixels[row][col] = (byte) Math.min(255, Math.max(0, pixelSum / kernelSum));
            }
        }

        return new Image(blurredPixels, image.label());
    }

}
