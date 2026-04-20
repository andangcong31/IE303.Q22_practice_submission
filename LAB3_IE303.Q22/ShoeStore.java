import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class ShoeStore extends JFrame {

    // ===== COLOR THEME =====
    private static final Color BG        = Color.WHITE;           // nền tổng: trắng
    private static final Color CARD_BG   = new Color(0xF5F5F5);  // card sản phẩm: xám nhạt
    private static final Color CARD_SEL  = new Color(0xDAEBFF);  // card được chọn: xanh nhạt
    private static final Color LEFT_BG   = Color.WHITE;          // panel trái: trắng

    private static final Color BORDER_SEL = new Color(0x0057FF); // viền card chọn: xanh
    private static final Color BORDER_DEF = new Color(0xDDDDDD); // viền card thường: xám nhạt

    private static final Color TEXT_TITLE = new Color(0x111111);
    private static final Color TEXT_PRICE = new Color(0x111111);
    private static final Color TEXT_BRAND = new Color(0x888888);
    private static final Color TEXT_DESC  = new Color(0xAAAAAA);

    // ===== CARD SIZE =====
    private static final int CARD_H = 220;

    // ===== SỐ SẢN PHẨM =====
    private static final int N = 20;

    // ===== DATA =====
    private static final String[] NAMES = {
        "4DFWD PULSE SHOES", "FORUM MID SHOES",  "SUPERNOVA SHOES",   "NMD CITY STOCK 2",
        "ULTRABOOST",        "ALPHABOUNCE",       "ZX 2K BOOST",       "ADIZERO",
        "4DFWD PULSE SHOES", "FORUM MID SHOES",   "SUPERNOVA SHOES",   "NMD CITY STOCK 2"
    };

    private static final String[] PRICES = {
        "$160.00","$100.00","$150.00","$160.00",
        "$120.00","$140.00","$130.00","$170.00",
        "$160.00","$100.00","$150.00","$160.00"
    };

    private static final String[] BRANDS = {
        "Adidas","Adidas","Adidas","Adidas",
        "Adidas","Adidas","Adidas","Adidas",
        "Adidas","Adidas","Adidas","Adidas"
    };

    private static final String[] DESCS = {
        "This product is excluded from all promotional discounts and offers.",
        "This product is excluded from all promotional discounts and offers.",
        "NMD City Stock 2",
        "NMD City Stock 2",
        "NMD City Stock 2",
        "This product is excluded from all promotional discounts and offers.",
        "This product is excluded from all promotional discounts and offers.",
        "This product is excluded from all promotional discounts and offers.",
        "This product is excluded from all promotional discounts and offers.",
        "This product is excluded from all promotional discounts and offers.",
        "NMD City Stock 2",
        "NMD City Stock 2"
    };

    private static final String IMG_DIR = "img";

    // ===== STATE =====
    private int selectedIndex = 0;
    private final BufferedImage[] images = new BufferedImage[6];

    private JLabel detailImage, detailName, detailPrice, detailBrand, detailDesc;
    private JPanel fadeWrap;
    private final JPanel[] cards = new JPanel[N];

    private Timer   fadeTimer;
    private float   alpha      = 1f;
    private boolean fadingOut  = false;
    private int     pendingIdx = -1;

    // ===== CONSTRUCTOR =====
    public ShoeStore() {
        super("Shoe Store");
        loadImages();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1200, 580);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout(12, 0));
        root.setBorder(new EmptyBorder(16, 16, 16, 16));
        root.setBackground(BG);

        root.add(buildLeftPanel(),  BorderLayout.WEST);
        root.add(buildRightPanel(), BorderLayout.CENTER);

        setContentPane(root);
        setVisible(true);
    }

    // ===== LOAD IMAGES =====
    private void loadImages() {
        for (int i = 0; i < 6; i++) {
            try {
                images[i] = ImageIO.read(new File(IMG_DIR + "/img" + (i + 1) + ".png"));
            } catch (Exception e) {
                images[i] = placeholder(i + 1);
            }
        }
    }

    private BufferedImage placeholder(int n) {
        BufferedImage img = new BufferedImage(300, 300, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setColor(new Color(0xEAEAEA));
        g.fillRect(0, 0, 300, 300);
        g.setColor(new Color(0x999999));
        g.setFont(new Font("SansSerif", Font.BOLD, 13));
        g.drawString("img/img" + n + ".png", 80, 105);
        g.dispose();
        return img;
    }

    private BufferedImage imgFor(int idx) {
        return images[idx % 6];
    }

    // Scale giữ tỉ lệ, chất lượng cao
    private ImageIcon scale(BufferedImage src, int maxW, int maxH) {
        if (src == null) return new ImageIcon();
        int ow = src.getWidth(), oh = src.getHeight();
        double ratio = Math.min((double) maxW / ow, (double) maxH / oh);
        int nw = (int)(ow * ratio), nh = (int)(oh * ratio);
        BufferedImage out = new BufferedImage(nw, nh, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = out.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING,     RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,  RenderingHints.VALUE_ANTIALIAS_ON);
        g2.drawImage(src, 0, 0, nw, nh, null);
        g2.dispose();
        return new ImageIcon(out);
    }

    // ===== LEFT PANEL =====
    private JPanel buildLeftPanel() {
        JPanel panel = new JPanel() {
            @Override public Dimension getPreferredSize() {
                return new Dimension(300, 0);
            }
        };

        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(LEFT_BG);
        panel.setBorder(new EmptyBorder(8, 8, 8, 16));

        // -- Ảnh (có hiệu ứng fade) --
        fadeWrap = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        fadeWrap.setAlignmentX(Component.LEFT_ALIGNMENT);
        fadeWrap.setMaximumSize(new Dimension(Integer.MAX_VALUE, 300));
        fadeWrap.setOpaque(false);
        fadeWrap.setBackground(LEFT_BG);

        detailImage = new JLabel();
        detailImage.setHorizontalAlignment(SwingConstants.CENTER);
        detailImage.setBorder(new EmptyBorder(0, 0, 10, 0));
        fadeWrap.add(detailImage, BorderLayout.CENTER);

        // -- Text info --
        detailName  = lbl("", 14, Font.BOLD,  TEXT_TITLE);
        detailPrice = lbl("", 22, Font.BOLD,  TEXT_PRICE);
        detailBrand = lbl("", 12, Font.PLAIN, TEXT_BRAND);
        detailDesc  = lbl("", 11, Font.PLAIN, TEXT_DESC);

        JPanel text = new JPanel();
        text.setAlignmentX(Component.LEFT_ALIGNMENT);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        text.setBackground(LEFT_BG);

        JSeparator sep = new JSeparator();
        sep.setForeground(BORDER_DEF);
        sep.setMaximumSize(new Dimension(Short.MAX_VALUE, 1));

        text.add(sep);
        text.add(Box.createVerticalStrut(10));
        text.add(detailName);
        text.add(Box.createVerticalStrut(4));
        text.add(detailPrice);
        text.add(Box.createVerticalStrut(3));
        text.add(detailBrand);
        text.add(Box.createVerticalStrut(6));
        text.add(detailDesc);

        panel.add(fadeWrap);
        panel.add(text);

        refreshDetail(0);
        return panel;
    }

    // ===== RIGHT PANEL =====
    private JScrollPane buildRightPanel() {
        JPanel grid = new JPanel(new GridLayout(0, 4, 10, 10));
        grid.setBackground(BG); // nền trắng
        grid.setBorder(new EmptyBorder(50, 0, 0, 0));

        for (int i = 0; i < N; i++) {
            cards[i] = buildCard(i);
            grid.add(cards[i]);
        }

        // Bọc NORTH để scrollbar hoạt động đúng
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(BG);
        wrapper.add(grid, BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(wrapper,
            ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
            ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setBorder(null);                         // KHÔNG có viền ngoài
        scroll.getViewport().setBackground(BG);
        scroll.getVerticalScrollBar().setUnitIncrement(24);
        return scroll;
    }

    // ===== BUILD CARD =====
    private JPanel buildCard(final int idx) {
        // Card dùng paintComponent để vẽ bo góc + viền
        JPanel card = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
            }
            @Override protected void paintBorder(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                boolean sel = (idx == selectedIndex);
                g2.setColor(sel ? BORDER_SEL : BORDER_DEF);
                g2.setStroke(new BasicStroke(sel ? 2f : 1f));
                g2.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, 10, 10);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(10, 10, 10, 10));
        card.setPreferredSize(new Dimension(0, CARD_H));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        card.setBackground(idx == selectedIndex ? CARD_SEL : CARD_BG);

        // Top: tên + mô tả
        JPanel top = new JPanel();
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        top.setOpaque(false);
        top.add(lbl(NAMES[idx % NAMES.length],  11, Font.BOLD,  TEXT_TITLE));
        top.add(lbl(clip(DESCS[idx % DESCS.length], 36), 10, Font.PLAIN, TEXT_DESC));

        // Centre: ảnh căn giữa
        JLabel img = new JLabel(scale(imgFor(idx), 160, 160));
        img.setHorizontalAlignment(SwingConstants.CENTER);
        JPanel center = new JPanel(new GridBagLayout());
        center.setOpaque(false);
        center.add(img);

        // Bottom: brand + giá
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setOpaque(false);
        bottom.add(lbl(BRANDS[idx % BRANDS.length], 10, Font.PLAIN, TEXT_BRAND), BorderLayout.WEST);
        bottom.add(lbl(PRICES[idx % PRICES.length], 13, Font.BOLD,  TEXT_PRICE), BorderLayout.EAST);

        card.add(top,    BorderLayout.NORTH);
        card.add(center, BorderLayout.CENTER);
        card.add(bottom, BorderLayout.SOUTH);

        card.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                if (idx != selectedIndex) { card.setBackground(new Color(0xEEF4FF)); card.repaint(); }
            }
            @Override public void mouseExited(MouseEvent e) {
                card.setBackground(idx == selectedIndex ? CARD_SEL : CARD_BG); card.repaint();
            }
            @Override public void mouseClicked(MouseEvent e) {
                if (idx != selectedIndex) doFade(idx);
            }
        });

        return card;
    }

    // ===== FADE SWITCH =====
    private void doFade(int newIdx) {
        if (fadeTimer != null && fadeTimer.isRunning()) fadeTimer.stop();
        int old = selectedIndex;
        pendingIdx = newIdx;
        fadingOut  = true;
        alpha      = 1f;

        cards[old].setBackground(CARD_BG);
        cards[old].repaint();

        fadeTimer = new Timer(15, e -> {
            if (fadingOut) {
                alpha -= 0.12f;
                if (alpha <= 0f) {
                    alpha     = 0f;
                    fadingOut = false;
                    selectedIndex = pendingIdx;
                    refreshDetail(selectedIndex);
                    cards[selectedIndex].setBackground(CARD_SEL);
                    cards[selectedIndex].repaint();
                }
            } else {
                alpha += 0.12f;
                if (alpha >= 1f) { alpha = 1f; fadeTimer.stop(); }
            }
            fadeWrap.repaint();
        });
        fadeTimer.start();
    }

    // ===== REFRESH LEFT =====
    private void refreshDetail(int idx) {
        detailImage.setIcon(scale(imgFor(idx), 260, 260));
        detailName .setText(NAMES[idx  % NAMES.length]);
        detailPrice.setText(PRICES[idx % PRICES.length]);
        detailBrand.setText(BRANDS[idx % BRANDS.length]);
        detailDesc .setText("<html><body style='width:240px'>"
                            + DESCS[idx % DESCS.length] + "</body></html>");
    }

    // ===== HELPERS =====
    private JLabel lbl(String text, int size, int style, Color color) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("SansSerif", style, size));
        l.setForeground(color);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private String clip(String s, int max) {
        return s.length() > max ? s.substring(0, max) + "..." : s;
    }

    // ===== MAIN =====
    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
        catch (Exception ignored) {}
        SwingUtilities.invokeLater(ShoeStore::new);
    }
}