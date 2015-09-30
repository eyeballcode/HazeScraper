import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import javax.swing.*;
import java.awt.*;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class WatcherThread extends Thread {

    static String lastPSI = "unknown";

    String lastTime = String.valueOf(GregorianCalendar.getInstance().get(Calendar.HOUR));

    JDialog main;

    JLabel label;

    public WatcherThread(JDialog main, JLabel label) {
        int hour = GregorianCalendar.getInstance().get(Calendar.HOUR_OF_DAY);
        this.main = main;
        this.label = label;
        if (hour > 12 && hour <= 23)
            lastTime += "pm";
        else
            lastTime += "am";

    }

    @Override
    public void run() {
        while (true) {
            try {
                Document document = Jsoup.connect("http://www.haze.gov.sg/haze-updates/psi-readings-over-the-last-24-hours").get();
                Element firstHalfOfDay = document.select(".noalter").get(1).children().select("tr").get(1);
                Element secondHalfOfDay = document.select(".noalter").get(1).children().select("tr").get(3);
//                System.out.println(secondHalfOfDay.html());
                firstHalfOfDay.select("*").get(0).remove();
                secondHalfOfDay.select("*").get(0).remove();
                int hour = GregorianCalendar.getInstance().get(Calendar.HOUR_OF_DAY);
                String currentPSI = "";
                //getPSI
                {
                    currentPSI = getPSI(firstHalfOfDay, secondHalfOfDay, hour, currentPSI, 0);
                }
                //checkPSI
                {
                    if (currentPSI.equals("-")) {
                        System.out.println("Current PSI at " + hour + " 00 isn't available, using cached PSI for " + (hour - 1) + " 00.");
                        hour--;
                        currentPSI = getPSI(firstHalfOfDay, secondHalfOfDay, hour, currentPSI, -1);
                    } else
                        lastPSI = currentPSI;
                }
                main.setPreferredSize(new Dimension(190, 30));
                main.pack();
                main.repaint();
                label.setText("  As of " + hour + " 00, the PSI is " + currentPSI);
                System.out.println("As of " + hour + " 00, the PSI is " + currentPSI);
                //sleep
                {
                    Thread.sleep(10000);
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (lastPSI.equals("Unknown"))
                    main.setPreferredSize(new Dimension(530, 30));
                else
                    main.setPreferredSize(new Dimension(430, 30));
                label.setText(" PSI: " + lastPSI + " at " + lastTime + ". However, the latest PSI could not be fetched.");
                main.pack();
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e1) {
                    // Ignore
                }
            }
        }
    }

    /*
    17 00 -> 10;
    16 00 -> 11;
    14 00 -> 12;
     */

//    private static int[] times = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 13, 12, 11, 10, 9, 8, 7, 6};

    private static String getPSI(Element firstHalfOfDay, Element secondHalfOfDay, int hour, String currentPSI, int offset) {
        if (hour == 12) {
            // Noon
            currentPSI = firstHalfOfDay.select("*").get(14 + offset).text();
        } else if (hour >= 1 && hour <= 11) {
            // Morning
            currentPSI = firstHalfOfDay.select("*").get(hour + offset).text();
        } else if (hour >= 13 && hour <= 23) {
            // Afternoon - night
            System.out.println(hour - 12);
            currentPSI = secondHalfOfDay.select("*").get(hour - 10).text();
            System.out.println(currentPSI);
        }
        return currentPSI;
    }

}
