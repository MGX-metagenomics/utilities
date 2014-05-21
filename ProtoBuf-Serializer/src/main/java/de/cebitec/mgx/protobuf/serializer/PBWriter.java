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

    private final Map<Message, byte[]> buffer = new ConcurrentHashMap<>();

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return Message.class.isAssignableFrom(type);
    }

    @Override
    public long getSize(Message m, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            m.writeTo(baos);
        } catch (IOException e) {
            Logger.getLogger(PBWriter.class.getName()).log(Level.SEVERE, null, e);
            return -1;
        }
        byte[] bytes = baos.toByteArray();
        buffer.put(m, bytes);

//        // test compression
//        try {
//            ByteArrayOutputStream tmp = new ByteArrayOutputStream();
//            GZIPOutputStream gzs = new GZIPOutputStream(tmp);
//            gzs.write(bytes);
//            gzs.flush();
//            int compressedSize = tmp.toByteArray().length;
//            System.err.println(m.getClass().getName() + " compressed: "+compressedSize+ " uncompressed: "+bytes.length);
//        } catch (IOException ex) {
//            Logger.getLogger(PBWriter.class.getName()).log(Level.SEVERE, null, ex);
//        }

        return bytes.length;
    }

    @Override
    public void writeTo(Message m, Class type, Type genericType, Annotation[] annotations,
            MediaType mediaType, MultivaluedMap httpHeaders,
            OutputStream entityStream) throws IOException, WebApplicationException {
        byte[] data = buffer.remove(m);
        if (data == null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try {
                m.writeTo(baos);
            } catch (IOException e) {
                Logger.getLogger(PBWriter.class.getName()).log(Level.SEVERE, null, e);
            }
            data = baos.toByteArray();
        }
        entityStream.write(data);
    }
}