package de.cebitec.mgx.protobuf.serializer;

import com.google.protobuf.Message;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.SSLHandshakeException;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

@Provider
@Produces("application/x-protobuf")
public class PBWriter implements MessageBodyWriter<Message> {

    @Override
    public final boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return Message.class.isAssignableFrom(type);
    }

    @Override
    public final long getSize(Message m, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return m.getSerializedSize();
    }

    @Override
    public final void writeTo(Message m, Class type, Type genericType, Annotation[] annotations,
            MediaType mediaType, MultivaluedMap httpHeaders,
            OutputStream entityStream) throws IOException, WebApplicationException {
        try {
            int retries = 3;
            while (retries > 0) {
                try {
                    m.writeTo(entityStream);
                    return;
                } catch (SSLHandshakeException ex) {
                    retries--;
                }
            }
        } catch (IOException e) {
            Logger.getLogger(PBWriter.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    /*
     * http://java-performance.info/various-methods-of-binary-serialization-in-java/
     */
    private static class PeekableBAOutputStream extends ByteArrayOutputStream {

        public byte[] getArray() {
            return this.buf;
        }
    }
}
