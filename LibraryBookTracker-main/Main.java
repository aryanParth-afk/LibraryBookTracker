package library;

import library.ui.MainWindow;
import library.ui.Theme;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Dark title bar on supported platforms
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        // Apply FlatLaf-style dark look-and-feel fallback using Nimbus
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) {}

        // Override key Nimbus colours for a dark theme
        UIManager.put("nimbusBase",            Theme.BG_DARK);
        UIManager.put("nimbusBlueGrey",        Theme.BG_SURFACE);
        UIManager.put("control",               Theme.BG_CARD);
        UIManager.put("text",                  Theme.TEXT_PRIMARY);
        UIManager.put("nimbusSelectedText",    Theme.TEXT_PRIMARY);
        UIManager.put("nimbusSelectionBackground", Theme.ACCENT);

        SwingUtilities.invokeLater(MainWindow::new);
    }
}
