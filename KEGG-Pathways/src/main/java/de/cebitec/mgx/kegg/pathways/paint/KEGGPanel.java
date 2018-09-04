package de.cebitec.mgx.kegg.pathways.paint;

import de.cebitec.mgx.kegg.pathways.KEGGException;
import de.cebitec.mgx.kegg.pathways.KEGGMaster;
import de.cebitec.mgx.kegg.pathways.api.ECNumberI;
import de.cebitec.mgx.kegg.pathways.api.PathwayI;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.SwingWorker;
import javax.swing.ToolTipManager;
import org.jfree.graphics2d.svg.SVGGraphics2D;

/**
 *
 * @author sj
 */
public class KEGGPanel extends JComponent {

    private transient BufferedImage image = null;
    private final transient KEGGMaster master;
    private final Map<Rectangle, String> toolTips;
    private int numDatasets = -1;
    private transient Map<ECNumberI, Collection<Rectangle>> coords = null;

    public KEGGPanel(KEGGMaster master) {
        super();
        this.master = master;
        toolTips = new HashMap<>();
        ToolTipManager.sharedInstance().registerComponent(this);
    }

    public void setPathway(final PathwayI pathway, int numSets) throws KEGGException {
        if (pathway == null) {
            return;
        }
        SwingWorker<BufferedImage, Void> worker = new SwingWorker<BufferedImage, Void>() {

            @Override
            protected BufferedImage doInBackground() throws Exception {
                return master.Pathways().getImage(pathway);
            }
        };
        worker.execute();
        try {
            image = worker.get();
        } catch (InterruptedException | ExecutionException ex) {
            Logger.getLogger(KEGGPanel.class.getName()).log(Level.SEVERE, null, ex);
            throw new KEGGException(ex);
        }
        int width = image.getWidth();
        int height = image.getHeight();
        setPreferredSize(new Dimension(width, height));
        coords = master.Pathways().getCoords(pathway);
        numDatasets = numSets;
        toolTips.clear();

        // default tooltips
        for (Entry<ECNumberI, Collection<Rectangle>> e : coords.entrySet()) {
            for (Rectangle r : e.getValue()) {
                toolTips.put(r, "<html><b>" + e.getKey().getNumber() + "</b><br><hr>");
            }
        }
    }

    public void addData(int datasetIdx, ECNumberI ecNum, Color color, String text) {
        assert datasetIdx < numDatasets;
        if (coords == null || !coords.containsKey(ecNum)) {
            return;
        }

        for (Rectangle r : coords.get(ecNum)) {
            int colRectWidth = r.width / numDatasets;
            int colRectX = r.x + datasetIdx * colRectWidth;
            int colRectHeight = r.height;
            Rectangle cr = new Rectangle(colRectX, r.y + r.height - colRectHeight, colRectWidth, colRectHeight);

            for (int x = cr.x; x <= cr.x + cr.width; x++) {
                for (int y = cr.y; y <= cr.y + cr.height; y++) {
                    int orig = image.getRGB(x, y);
                    image.setRGB(x, y, orig & color.getRGB());
                }
            }

            toolTips.remove(r);
            toolTips.put(cr, text);
        }

    }

//   TOO SLOW!   
//    private Image addColor(BufferedImage im) {
//        ImageFilter filter = new RGBImageFilter() {
//            @Override
//            public final int filterRGB(int x, int y, int rgb) {
//
//                for (ColoredRectangle cr : rects.keySet()) {
//                    if (cr.contains(x, y)) {
//                        return rgb & cr.getColor().getRGB();
//                    }
//                }
//                return rgb;
//            }
//        };
//
//        ImageProducer ip = new FilteredImageSource(im.getSource(), filter);
//        return Toolkit.getDefaultToolkit().createImage(ip);
//    }
//    private static BufferedImage addColor(BufferedImage im, Set<ColoredRectangle> paintMe) {
//
//        for (ColoredRectangle cr : paintMe) {
//            for (int x = cr.x; x <= cr.x + cr.width; x++) {
//                for (int y = cr.y; y <= cr.y + cr.height; y++) {
//                    int orig = im.getRGB(x, y);
//                    im.setRGB(x, y, orig & cr.getColor().getRGB());
//                }
//            }
//        }
//        return im;
//    }
    public void savePNG(File target) throws IOException {
        ImageIO.write(image, "png", target);
    }

    public void saveJPEG(File target) throws IOException {
        ImageIO.write(image, "jpg", target);
    }

    public void saveSVG(File target) throws IOException {
        SVGGraphics2D g2 = new SVGGraphics2D(image.getWidth(), image.getHeight());
        g2.drawImage(image, 0, 0, null);
        String svgElement = g2.getSVGElement();
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(target))) {
            bw.write(svgElement);
        }
    }

    @Override
    public String getToolTipText(MouseEvent event) {
        for (Entry<? extends Rectangle, String> e : toolTips.entrySet()) {
            if (e.getKey().contains(event.getX(), event.getY())) {
                return e.getValue();
            }
        }
        return super.getToolTipText(event);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        super.paintComponent(g2);
        g2.drawImage(image, 0, 0, null);
    }
}
