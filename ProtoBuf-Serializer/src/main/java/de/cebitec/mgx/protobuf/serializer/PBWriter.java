package de.cebitec.mgx.protobuf.serializer;

import com.google.protobuf.Message;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

@Provider
@Produces("application/x-protobuf")
public class PBWriter implements MessageBodyWriter<Message> {

//    private final Map<Message, byte[]> buffer = new ConcurrentHashMap<>();
    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return Message.class.isAssignableFrom(type);
    }

    @Override
    public long getSize(Message m, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return m.getSerializedSize();
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        try {
//            m.writeTo(baos);
//        } catch (IOException e) {
//            Logger.getLogger(PBWriter.class.getName()).log(Level.SEVERE, null, e);
//            return -1;
//        }
//        byte[] bytes = baos.toByteArray();
//        buffer.put(m, bytes);
//
//        int serSize = m.getSerializedSize();
//        int baoSize = baos.size();
//        if (serSize != baoSize || bytes.length != baoSize || serSize != bytes.length) {
//            Logger.getLogger(PBWriter.class.getName()).log(Level.INFO, "serialized size difference encountered: {0} vs {1} vs {2}", new Object[]{serSize, baoSize, bytes.length});
//        }
//
//        return bytes.length;
    }

    @Override
    public void writeTo(Message m, Class type, Type genericType, Annotation[] annotations,
            MediaType mediaType, MultivaluedMap httpHeaders,
            OutputStream entityStream) throws IOException, WebApplicationException {
//        byte[] data = buffer.remove(m);
//        if (data == null) {
        try {
            m.writeTo(entityStream);
        } catch (IOException e) {
            Logger.getLogger(PBWriter.class.getName()).log(Level.SEVERE, null, e);
        }
//        } else {
//            entityStream.write(data);
//        }
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
