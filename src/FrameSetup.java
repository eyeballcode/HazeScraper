import javax.script.ScriptEngineManager;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.URI;
import java.util.HashMap;

public class FrameSetup {

    static HashMap<Integer, Boolean> pressedKeys = new HashMap<Integer, Boolean>();


    static int pX = 0;
    static int pY = 0;

    public static void setupFrame(final JDialog frame, JRootPane root) {
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        if (System.getProperty("os.name").toLowerCase().contains("mac os"))
            frame.setLocation(0, 20);
        else
            frame.setLocation(0, 0);
        frame.setAlwaysOnTop(true);
        frame.setUndecorated(true);
        frame.setOpacity(0.86f);
        frame.setBackground(Color.WHITE);
        frame.addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent mouseWheelEvent) {
                if (mouseWheelEvent.getWheelRotation() < 0) {
                    if (frame.getOpacity() + .1f > 1f)
                        return;
                    frame.setOpacity(frame.getOpacity() + 0.1f);
                    if (frame.getOpacity() > 0.1f) {
                        frame.getContentPane().setBackground(Color.WHITE);
                        frame.getContentPane().setForeground(Color.BLACK);
                        frame.repaint();
                    }
                } else {
                    if (frame.getOpacity() - .1f < 0f)
                        return;
                    if (frame.getOpacity() - 0.1f <= 0.1f) {
                        frame.getContentPane().setBackground(Color.RED);
                        frame.getContentPane().setForeground(Color.WHITE);
                    }

                    frame.setOpacity(frame.getOpacity() - 0.1f);
                    frame.repaint();
                }
            }
        });
        frame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                pressedKeys.put(e.getKeyCode(), true);
            }

            @Override
            public void keyReleased(KeyEvent e) {
                pressedKeys.put(e.getKeyCode(), false);
            }
        });
        frame.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent me) {
                pX = me.getX();
                pY = me.getY();
                int target = 0;
                if (System.getProperty("os.name").toLowerCase().contains("mac os"))
                    target = MouseEvent.BUTTON1;
                else
                    target = MouseEvent.BUTTON3;
                if (me.getButton() == target) {
                    try {
                        if (!Desktop.isDesktopSupported()) {
                            JOptionPane.showMessageDialog(null, "You don't have java.awt.desktop... :(", "Can't open link", JOptionPane.INFORMATION_MESSAGE);
                        }
                        Desktop desktop = Desktop.getDesktop();
                        if (!System.getProperty("os.name").toLowerCase().contains("mac")) {
                            if (pressedKeys.get(KeyEvent.VK_SHIFT) != null && pressedKeys.get(KeyEvent.VK_SHIFT)) {
                                desktop.browse(new URI("https://github.com/eyeballcode/hazescraper"));
                                pressedKeys.put(KeyEvent.VK_SHIFT, false);
                            } else {
                                desktop.browse(new URI("http://www.haze.gov.sg/haze-updates/psi-readings-over-the-last-24-hours"));
                            }
                        } else {
                            if (pressedKeys.get(KeyEvent.VK_META) != null && pressedKeys.get(KeyEvent.VK_META)) {
                                if (pressedKeys.get(KeyEvent.VK_SHIFT) != null && pressedKeys.get(KeyEvent.VK_SHIFT)) {
                                    desktop.browse(new URI("https://github.com/eyeballcode/hazescraper"));
                                    pressedKeys.put(KeyEvent.VK_SHIFT, false);
                                    pressedKeys.put(KeyEvent.VK_META, false);
                                } else {
                                    desktop.browse(new URI("http://www.haze.gov.sg/haze-updates/psi-readings-over-the-last-24-hours"));
                                    pressedKeys.put(KeyEvent.VK_META, false);
                                }
                            }
                        }
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(null, "Error while opening link: " + e.getClass().toString() + ": " + e.getMessage(),
                                "Can't open link", JOptionPane.INFORMATION_MESSAGE);
                    }

                }
            }

            public void mouseDragged(MouseEvent me) {
                frame.setLocation(frame.getLocation().x + me.getX() - pX,
                        frame.getLocation().y + me.getY() - pY);
            }
        });

        frame.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent me) {
                frame.setLocation(frame.getLocation().x + me.getX() - pX,
                        frame.getLocation().y + me.getY() - pY);
            }
        });
        KeyStroke close = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false);
        Action action = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                int close = JOptionPane.showConfirmDialog(null, "Are you sure you want to exit?", "Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                if (close == 0)
                    System.exit(0);
            }
        };
        root
                .getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(close, "ESCAPE");
        root.getActionMap().put("ESCAPE", action);
        root.putClientProperty("Window.shadow", root);
        frame.setPreferredSize(new Dimension(190, 30));
        pressedKeys.put(KeyEvent.VK_SHIFT, false);
    }
}
