import java.awt.Toolkit;

public class SoundManager {
    private boolean enabled = true;

    public void setEnabled(boolean e) { enabled = e; }
    public boolean isEnabled() { return enabled; }

    public void beep() {
        if (!enabled) return;
        try {
            Toolkit.getDefaultToolkit().beep();
        } catch (Exception ex) {
            // ignore
        }
    }
}
