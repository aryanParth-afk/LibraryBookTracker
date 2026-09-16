package library.ui;

import java.awt.*;

public class Theme {
    // ── Palette ───────────────────────────────────────────────
    public static final Color BG_DARK      = new Color(13,  17,  23);
    public static final Color BG_CARD      = new Color(22,  27,  34);
    public static final Color BG_SURFACE   = new Color(30,  37,  46);
    public static final Color ACCENT       = new Color(88, 166, 255);
    public static final Color ACCENT_SOFT  = new Color(56, 139, 253, 60);
    public static final Color SUCCESS      = new Color(63, 185, 80);
    public static final Color WARNING      = new Color(210,153, 34);
    public static final Color DANGER       = new Color(248, 81, 73);
    public static final Color TEXT_PRIMARY = new Color(230,237,243);
    public static final Color TEXT_MUTED   = new Color(139,148,158);
    public static final Color BORDER       = new Color(48,  54,  61);

    // ── Fonts ─────────────────────────────────────────────────
    public static final Font FONT_TITLE  = new Font("Segoe UI", Font.BOLD,  22);
    public static final Font FONT_HEADING= new Font("Segoe UI", Font.BOLD,  14);
    public static final Font FONT_BODY   = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_SMALL  = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_MONO   = new Font("Consolas",  Font.PLAIN, 12);

    // ── Corner radius ─────────────────────────────────────────
    public static final int RADIUS = 10;
}
