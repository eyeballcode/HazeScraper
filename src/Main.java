import javax.swing.*;
import java.io.IOException;

public class Main {

    // jQuery for selector the PSI values.
    // $($('table.noalter')[1]).children().children().not('.even').children()


    public static void main(String[] a) throws IOException {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception exp) {
            // Ignore
        }

        final JDialog main = new JDialog();
        main.setTitle("Haze!");
        final JLabel label = new JLabel("Please wait..");
        main.add(label);
        FrameSetup.setupFrame(main, main.getRootPane());
        main.pack();
        new WatcherThread(main, label).start();
        main.setVisible(true);
    }


}

