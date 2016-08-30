//package de.cebitec.mgx.kegg.pathways.paint;
//
//import java.awt.Color;
//import java.awt.Rectangle;
//import java.util.Objects;
//
///**
// *
// * @author sj
// */
//public class ColoredRectangle extends Rectangle {
//
//    private final Color color;
//
//    public ColoredRectangle(Color color, int x, int y, int width, int height) {
//        super(x, y, width, height);
//        this.color = color;
//    }
//
//    public ColoredRectangle(Color color, Rectangle r) {
//        super(r);
//        this.color = color;
//    }
//
//    public Color getColor() {
//        return color;
//    }
//
//    @Override
//    public int hashCode() {
//        int hash = 7;
//        hash = 37 * hash + super.hashCode();
//        hash = 37 * hash + Objects.hashCode(this.color);
//        return hash;
//    }
//
//    @Override
//    public boolean equals(Object obj) {
//        if (this == obj) {
//            return true;
//        }
//        if (obj == null) {
//            return false;
//        }
//        if (getClass() != obj.getClass()) {
//            return false;
//        }
//        final ColoredRectangle other = (ColoredRectangle) obj;
//        if (!Objects.equals(this.color, other.color)) {
//            return false;
//        }
//        if (this.x != other.x) {
//            return false;
//        }
//        if (this.y != other.y) {
//            return false;
//        }
//        if (this.width != other.width) {
//            return false;
//        }
//        if (this.height != other.height) {
//            return false;
//        }
//        return true;
//    }
//
//}
