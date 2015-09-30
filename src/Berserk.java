import java.util.HashMap;

public class Berserk {

    static int tileSize = 256;
    static double initialResolution = 2d * Math.PI * 6378137d / (double) tileSize;
    // 156543.03392804062 for tileSize 256 PIxels
    static double originShift = 2d * Math.PI * 6378137d / 2d;

    /**
     * Converts given lat/lon in WGS84 Datum to XY in Spherical Mercator EPSG:900913
     *
     * @param lat The latitude
     * @param lon The longitude
     * @return A hashmap containing the x meter and y meter.
     */
    public static HashMap<String, Double> latLonToMeters(double lat, double lon) {
        HashMap<String, Double> returnVals = new HashMap<String, Double>();

        double mx = lon * originShift / 180.0;
        double my = Math.log(Math.tan((90 + lat) * Math.PI / 360.0)) / (Math.PI / 180.0);

        my = my * originShift / 180.0;
        returnVals.put("mx", mx);
        returnVals.put("my", my);
        return returnVals;
    }

    /**
     * Converts EPSG:900913 to pyramid pixel coordinates in given zoom level
     *
     * @param mx         The meter x
     * @param my         The meter y
     * @param resolution The resolution
     * @return A hashmap containg the x pixel and y pixel.
     */
    public static HashMap<String, Double> metersToPixels(double mx, double my, double resolution) {
        HashMap<String, Double> returnVals = new HashMap<String, Double>();

        double px = (mx + originShift) / resolution;
        double py = (my + originShift) / resolution;

        returnVals.put("px", px);
        returnVals.put("py", py);
        return returnVals;
    }

    public static void main(String[] args) {
        double mx = -8237494.4864285; //-73.998672
        double my = 4970354.7325767; // 40.714728
        double resolution = 12;
        HashMap<String, Double> meterValues = metersToPixels(mx, my, resolution);
        HashMap<String, Double> pixelValues = latLonToMeters(meterValues.get("px"), meterValues.get("py"));
        System.out.println("First val: " + pixelValues.get("my"));
        System.out.println("Second val: " + pixelValues.get("mx"));
    }

}
