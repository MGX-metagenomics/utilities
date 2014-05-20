
package de.cebitec.mgx.newick;

import de.cebitec.mgx.newick.impl.NewickParserImpl;
import de.cebitec.mgx.newick.impl.ParseException;
import java.io.InputStream;



/**
 *
 * @author sjaenick
 */
public class NewickParser {
    
    public static NodeI parse(String nwk) throws ParserException {
        try {
            return new NewickParserImpl(nwk).tree();
        } catch (ParseException ex) {
            throw new ParserException(ex);
        }
    }

    public static NodeI parse(InputStream is) throws ParserException {
        try {
            return new NewickParserImpl(is).tree();
        } catch (ParseException ex) {
            throw new ParserException(ex);
        }
    }
}
