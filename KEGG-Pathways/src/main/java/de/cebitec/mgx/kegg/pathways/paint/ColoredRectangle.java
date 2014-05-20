
package de.cebitec.mgx.kegg.pathways.paint;

import java.awt.Color;
import java.awt.Rectangle;

/**
 *
 * @author sj
 */
public class ColoredRectangle extends Rectangle {
    
    private final Color color;

    public ColoredRectangle(Color color, int x, int y, int width, int height) {
        super(x, y, width, height);
        this.color = color;
    }

    public ColoredRectangle(Color color, Rectangle r) {
        super(r);
        this.color = color;
    }

    public Color getColor() {
        return color;
    }
    
}
