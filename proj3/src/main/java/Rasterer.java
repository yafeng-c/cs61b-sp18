import java.util.HashMap;
import java.util.Map;

/**
 * This class provides all code necessary to take a query box and produce
 * a query result. The getMapRaster method must return a Map containing all
 * seven of the required fields, otherwise the front end code will probably
 * not draw the output correctly.
 */
public class Rasterer {
    private double ullon;
    private double ullat;
    private double lrlon;
    private double lrlat;
    private double INITLRLON;
    private double INITULLON;
    private double INITLRLAT;
    private double INITULLAT;
    private int TILESIZE;
    private double[] lonDPPs;

    public Rasterer() {
        // YOUR CODE HERE
        INITLRLON = MapServer.ROOT_LRLON;
        INITULLON = MapServer.ROOT_ULLON;
        INITLRLAT = MapServer.ROOT_LRLAT;
        INITULLAT = MapServer.ROOT_ULLAT;
        TILESIZE = MapServer.TILE_SIZE;
        lonDPPs = new double[8];
        lonDPPs[0] = (INITLRLON - INITULLON) / TILESIZE;
        // smallest LonDPP, e.g. depth 7 images
        for (int i = 1; i < 8; i++) {
            lonDPPs[i] = lonDPPs[i - 1] / 2;
        }
    }

    /**
     * Takes a user query and finds the grid of images that best matches the query. These
     * images will be combined into one big image (rastered) by the front end. <br>
     *
     *     The grid of images must obey the following properties, where image in the
     *     grid is referred to as a "tile".
     *     <ul>
     *         <li>The tiles collected must cover the most longitudinal distance per pixel
     *         (LonDPP) possible, while still covering less than or equal to the amount of
     *         longitudinal distance per pixel in the query box for the user viewport size. </li>
     *         <li>Contains all tiles that intersect the query bounding box that fulfill the
     *         above condition.</li>
     *         <li>The tiles must be arranged in-order to reconstruct the full image.</li>
     *     </ul>
     *
     * @param params Map of the HTTP GET request's query parameters - the query box and
     *               the user viewport width and height.
     *
     * @return A map of results for the front end as specified: <br>
     * "render_grid"   : String[][], the files to display. <br>
     * "raster_ul_lon" : Number, the bounding upper left longitude of the rastered image. <br>
     * "raster_ul_lat" : Number, the bounding upper left latitude of the rastered image. <br>
     * "raster_lr_lon" : Number, the bounding lower right longitude of the rastered image. <br>
     * "raster_lr_lat" : Number, the bounding lower right latitude of the rastered image. <br>
     * "depth"         : Number, the depth of the nodes of the rastered image <br>
     * "query_success" : Boolean, whether the query was able to successfully complete; don't
     *                    forget to set this to true on success! <br>
     */
    public Map<String, Object> getMapRaster(Map<String, Double> params) {
        // System.out.println(params);
        Map<String, Object> results = new HashMap<>();
//        System.out.println("Since you haven't implemented getMapRaster, nothing is displayed in "
//                           + "your browser.");
        ullon = params.get("ullon");
        ullat = params.get("ullat");
        lrlon = params.get("lrlon");
        lrlat = params.get("lrlat");
        double width = params.get("w");
        double height = params.get("h");

        if (ullon >= INITLRLON || ullat <= INITLRLAT
            || lrlon <= INITULLON || lrlat >= INITULLAT
            || ullon >= lrlon || ullat <= lrlat) {
            results.put("render_grid", null);
            results.put("raster_ul_lon", 0);
            results.put("raster_ul_lat", 0);
            results.put("raster_lr_lon", 0);
            results.put("raster_lr_lat", 0);
            results.put("depth", 0);
            results.put("query_success", false);
            return results;
        }
        double reqLonDPP = (lrlon - ullon) / width;
        int depth = getDepth(reqLonDPP);
        // at the Dth level of zoom, there are 4^D images, with names
        // ranging from dD_x0_y0 to dD_xk_yk, where k is 2^D - 1.
        double k = Math.pow(2, depth) - 1;
        // As x increases from 0 to k, we move eastwards,
        // and as y increases from 0 to k, we move southwards.
        // dD_x0_y0 longitude ranges from INITULLON to INITULLON + xStep
        // latitude ranges from INITLRLAT to INITLRLAT + yStep
        double xStep = (INITLRLON - INITULLON) / (k + 1);
        double yStep = (INITLRLAT - INITULLAT) / (k + 1);
        int xUL = 0, yUL = 0, xLR = 0, yLR = 0;
        for (double i = INITULLON; i < INITLRLON; i += xStep) {
            if (i <= ullon && ullon < i + xStep) {
                break;
            }
            xUL++;
        }
        for (double i = INITULLAT; i > INITLRLAT; i += yStep) {
            if (i >= ullat && ullat > i + yStep) {
                break;
            }
            yUL++;
        }
        for (double i = INITULLON; i < INITLRLON; i += xStep) {
            if (i <= lrlon && lrlon < i + xStep) {
                break;
            }
            xLR++;
        }
        for (double i = INITULLAT; i > INITLRLAT; i += yStep) {
            if (i >= lrlat && lrlat > i + yStep) {
                break;
            }
            yLR++;
        }
        String[][] files = new String[yLR - yUL + 1][xLR - xUL + 1];
        for (int y = yUL; y <= yLR; y++) {
            for (int x = xUL; x <= xLR; x++) {
                files[y - yUL][x - xUL] = "d" + depth + "_x" + x + "_y" + y + ".png";
            }
        }
        results.put("render_grid", files);
        results.put("raster_ul_lon", INITULLON + xUL * xStep);
        results.put("raster_ul_lat", INITULLAT + yUL * yStep);
        results.put("raster_lr_lon", INITULLON + (xLR + 1) * xStep);
        results.put("raster_lr_lat", INITULLAT + (yLR + 1) * yStep);
        results.put("depth", depth);
        results.put("query_success", true);
        return results;
    }

    private int getDepth(double reqLonDPP) {
        int depth = 0;
        for (double l : lonDPPs) {
            if (reqLonDPP > l) {
                break;
            }
            depth++;
        }
        if (depth == lonDPPs.length) {
            depth -= 1;
        }
        return depth;
    }

}
