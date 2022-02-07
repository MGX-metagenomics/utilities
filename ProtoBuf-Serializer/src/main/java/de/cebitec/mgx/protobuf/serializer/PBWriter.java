package de.cebitec.mgx.protobuf.serializer;

import com.google.protobuf.GeneratedMessageV3;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPOutputStream;
import javax.net.ssl.SSLHandshakeException;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.MessageBodyWriter;
import jakarta.ws.rs.ext.Provider;

@Provider
@Produces("application/x-protobuf")
public class PBWriter implements MessageBodyWriter<GeneratedMessageV3> {

    @Override
    public final boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return GeneratedMessageV3.class.isAssignableFrom(type);
    }

    @Override
    public final long getSize(GeneratedMessageV3 m, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return -1;
    }

    @Override
    public final void writeTo(GeneratedMessageV3 m, Class type, Type genericType, Annotation[] annotations,
            MediaType mediaType, MultivaluedMap httpHeaders,
            OutputStream entityStream) throws IOException, WebApplicationException {

        PeekableBAOutputStream bao = new PeekableBAOutputStream(65535);
        try (GZIPOutputStream gzo = new GZIPOutputStream(bao, 65535)) {
            m.writeTo(gzo); 
            gzo.flush();
            bao.flush();
        }

        try {
            int retries = 3;
            while (retries > 0) {
                try {
                    entityStream.write(bao.getArray());
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

        public PeekableBAOutputStream(int size) {
            super(size);
        }

        public byte[] getArray() {
            // avoid copy
            return this.buf;
        }
    }
}
