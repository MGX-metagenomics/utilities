/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.newick;

import java.util.Iterator;

/**
 *
 * @author sj
 */
public class NewickWriter {

    public static String toNewick(NodeI root) {
        StringBuilder sb = new StringBuilder();
        sb = appendNode(sb, root);
        sb = sb.append(";");
        return sb.toString();
    }

    private static StringBuilder appendNode(StringBuilder sb, NodeI node) {
        if (!node.isLeaf()) {
            sb = sb.append("(");
            sb = join(sb, node.getChildren());
            sb = sb.append(")");
        }
        sb = sb.append(node.getName());
        sb = sb.append(":");
        sb = sb.append(node.getWeight());
        return sb;
    }

    private static StringBuilder join(StringBuilder sb, Iterable< NodeI> pColl) {
        Iterator<NodeI> oIter;
        if (pColl == null || (!(oIter = pColl.iterator()).hasNext())) {
            return sb;
        }
        sb = appendNode(sb, oIter.next());
        while (oIter.hasNext()) {
            sb = sb.append(",");
            sb = appendNode(sb, oIter.next());
        }
        return sb;
    }
}
