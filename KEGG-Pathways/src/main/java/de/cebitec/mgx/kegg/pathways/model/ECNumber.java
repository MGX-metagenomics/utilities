package de.cebitec.mgx.kegg.pathways.model;

import de.cebitec.mgx.kegg.pathways.api.ECNumberI;
import java.util.Objects;

/**
 *
 * @author sj
 */
public class ECNumber  implements ECNumberI {

    private final String number;

    public ECNumber(String number) {
        this.number = number;
    }

    @Override
    public String getNumber() {
        return number;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 19 * hash + Objects.hashCode(this.number);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ECNumber other = (ECNumber) obj;
        if (!Objects.equals(this.number, other.number)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return number;
    }

    @Override
    public int compareTo(ECNumberI o) {
        String[] myEC = number.split("[.]");
        String[] other = o.getNumber().split("[.]");
        for (int i = 0; i < 4; i++) {
            if (myEC[i].equals("-")) {
                if (other[i].equals("-")) {
                    return 0; // both "-"
                } else {
                    return 1;  // "-" vs number
                }
            } else if (other[i].equals("-")) {
                if (myEC[i].equals("-")) {
                    return 0; // both "-"
                } else {
                    return -1;  // "-" vs number
                }
            } else {
                int myInt = Integer.parseInt(myEC[i]);
                int otherInt = Integer.parseInt(other[i]);
                if (myInt != otherInt) {
                    return Integer.compare(myInt, otherInt);
                }
            }
        }
        return 0;
    }
}
