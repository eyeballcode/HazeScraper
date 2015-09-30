import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import javax.swing.*;
import java.io.IOException;
import java.util.List;

public class Main {

    // jQuery for selector the PSI values.
    // $($('table.noalter')[1]).children().children().not('.even').children()


    public static void main(String[] a) throws IOException {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception exp) {
            JOptionPane.showMessageDialog(null, "Could not set LAF to the system, using metal.", "Error", JOptionPane.ERROR_MESSAGE);
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

