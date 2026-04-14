import javax.swing.SwingUtilities;

public class Preview {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new AnnonceurGUI(41).setVisible(true);
                new AcheteurGUI(1).setVisible(true);
                Main.main(new String[0]);
            }
        });
    }
}
