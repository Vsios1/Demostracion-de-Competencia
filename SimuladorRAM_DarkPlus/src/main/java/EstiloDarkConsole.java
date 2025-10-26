import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class EstiloDarkConsole {
    public static final Color BG = new Color(0x1E1E1E);
    public static final Color PANEL = new Color(0x252526);
    public static final Color TEXT = new Color(0xD4D4D4);
    public static final Color ACCENT = new Color(0x007ACC);
    public static final Color CPU = new Color(0x569CD6);
    public static final Color RAM = new Color(0xC586C0);
    public static final Color LOG = new Color(0x9CDCFE);

    public static void aplicarEstilo(JFrame frame) {
        frame.getContentPane().setBackground(BG);
        frame.getContentPane().setFont(new Font("Segoe UI", Font.PLAIN, 13));
    }

    public static JButton styledButton(String text, Color bg) {
        JButton b = new JButton(text);
        b.setBackground(bg);
        b.setForeground(TEXT);
        b.setFocusPainted(false);
        b.setBorder(new EmptyBorder(6,12,6,12));
        return b;
    }
}
