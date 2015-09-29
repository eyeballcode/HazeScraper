import com.sun.awt.AWTUtilities;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class Main {
    // jQuery for selector the PSI vals.
    // $($('table.noalter')[1]).children().children().not('.even').children()

    static int pX;
    static int pY = 0;
    static String lastPSI = "Unknown", lastTime = String.valueOf(GregorianCalendar.getInstance().get(Calendar.HOUR));

    public static void main(String[] a) throws IOException {
        int hour = GregorianCalendar.getInstance().get(Calendar.HOUR_OF_DAY);
        System.out.println("Hour: " + hour + " " + (hour > 12 && hour <= 23));
        if (hour > 12 && hour <= 23)
            lastTime += "pm";
        else
            lastTime += "am";
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception exp) {
        }

        final JDialog main = new JDialog();
//        final JFrame main = new JFrame();
        main.setTitle("Haze!");
        JRootPane root = main.getRootPane();
        root.putClientProperty("Window.shadow", root);
        main.setPreferredSize(new Dimension(190, 30));
        final JLabel label = new JLabel("Please wait..");
        main.add(label);
        main.setResizable(false);
        main.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        main.setLocation(0, 0);
        main.setAlwaysOnTop(true);
        main.setUndecorated(true);
        main.setOpacity(0.7f);
        main.setBackground(Color.WHITE);
//        AWTUtilities.setWindowOpaque(main, false);
        main.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent me) {
                pX = me.getX();
                pY = me.getY();

            }

            public void mouseDragged(MouseEvent me) {
                main.setLocation(main.getLocation().x + me.getX() - pX,
                        main.getLocation().y + me.getY() - pY);
            }
        });

        main.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent me) {
                main.setLocation(main.getLocation().x + me.getX() - pX,
                        main.getLocation().y + me.getY() - pY);
            }
        });
        KeyStroke close = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false);
        Action action = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                int close = JOptionPane.showConfirmDialog(null, "Are you sure you want to exit?", "Confimation", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                if (close == 0)
                    System.exit(0);
            }
        };
        root
                .getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(close, "ESCAPE");
        root.getActionMap().put("ESCAPE", action);
        main.pack();
        new Thread() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Document document = Jsoup.connect("http://www.haze.gov.sg/haze-updates/psi-readings-over-the-last-24-hours").get();
                        Element hour12 = document.select(".noalter").get(1).children().select("tr").not(".even").get(0);
                        Element hour24 = document.select(".noalter").get(1).children().select("tr").not(".even").get(1);
                        main.revalidate();
                        int hour = GregorianCalendar.getInstance().get(Calendar.HOUR_OF_DAY);
                        int timeIn12Hour = GregorianCalendar.getInstance().get(Calendar.HOUR);
                        System.out.println(hour);
                        System.out.println(timeIn12Hour);
                        if (hour > 12 && hour <= 23) {
                            String psi24 = hour24.select("td").get(hour - 12).text();
                            if (psi24.equals("-") && lastPSI.equals("Unknown")) {
                                psi24 = "Unknown";
                                main.setPreferredSize(new Dimension(220, 30));
                            } else if (psi24.equals("-")) {
                                timeIn12Hour--;
                                psi24 = "Unknown";
                                main.setPreferredSize(new Dimension(220, 30));
                            } else {
                                main.setPreferredSize(new Dimension(190, 30));
                                psi24 = hour24.select("td").get(hour - 12).text();
                                System.out.println(hour24.select("td").get(hour - 12).text());
                            }
                            lastPSI = psi24;
                            main.pack();
                            label.setText("  As of " + timeIn12Hour + "pm, the PSI is " + psi24);
                            main.repaint();
                            System.out.println("As of " + timeIn12Hour + "pm, the PSI is " + psi24);
                            lastTime = timeIn12Hour + "pm";
                        } else {
                            String psi12 = hour12.select("td").get(hour - 3).text();
                            if (psi12.equals("-") && lastPSI.equals("Unknown")) {
                                psi12 = "Unknown";
                                main.setPreferredSize(new Dimension(220, 30));
                            } else if (psi12.equals("-")) {
                                timeIn12Hour--;
                                psi12 = "Unknown";
                                main.setPreferredSize(new Dimension(220, 30));
                            } else {
                                main.setPreferredSize(new Dimension(190, 30));
                                psi12 = hour24.select("td").get(hour - 3).text();
                            }
                            lastPSI = psi12;
                            main.pack();
                            label.setText("  As of " + timeIn12Hour + "am, the PSI is " + psi12);
                            System.out.println("As of " + timeIn12Hour + "am, the PSI is " + psi12);
                            lastTime = timeIn12Hour + "am";
                        }
                        Thread.sleep(10000);
                    } catch (Exception e) {
                        if (lastPSI.equals("Unknown"))
                            main.setPreferredSize(new Dimension(530, 30));
                        else
                            main.setPreferredSize(new Dimension(430, 30));
                        label.setText(" PSI: " + lastPSI + " at " + lastTime + ". However, the latest PSI could not be fetched.");
                        main.pack();
                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException e1) {
                        }
                    }
                }
            }
//        };
        }.start();
        main.setVisible(true);
    }
}

