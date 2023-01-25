package prefuse.svg;

import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import org.jfree.svg.SVGGraphics2D;
import prefuse.Display;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author sjaenick
 *
 */
public class SVGDisplaySaver {

    private SVGDisplaySaver() {
    }

    public static boolean saveSVG(Display dis, OutputStream out, double scale) {

        Dimension d = new Dimension((int) (scale * dis.getWidth()),
                (int) (scale * dis.getHeight()));

        SVGGraphics2D svgG = new SVGGraphics2D((int) d.getWidth(), (int) d.getHeight());

        Point2D p = new Point2D.Double(0, 0);
        dis.zoom(p, scale);
        boolean q = dis.isHighQuality();
        dis.setHighQuality(true);
        dis.paintDisplay(svgG, d);
        dis.setHighQuality(q);
        dis.zoom(p, 1 / scale);

        try ( Writer wr = new OutputStreamWriter(out, "UTF-8")) {
            wr.write(svgG.getSVGDocument());
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
