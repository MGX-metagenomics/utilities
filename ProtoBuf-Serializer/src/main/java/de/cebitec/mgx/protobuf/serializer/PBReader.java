package de.cebitec.mgx.protobuf.serializer;

import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.Message;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.zip.GZIPInputStream;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.ext.MessageBodyReader;
import jakarta.ws.rs.ext.Provider;

@Provider
@Consumes("application/x-protobuf")
public class PBReader implements MessageBodyReader<Message> {

    @Override
    public final boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return Message.class.isAssignableFrom(type);
    }

    @Override
    public final Message readFrom(Class<Message> type, Type genericType, Annotation[] annotations,
            MediaType mediaType, MultivaluedMap<String, String> httpHeaders,
            InputStream entityStream) throws IOException, WebApplicationException {
        try (GZIPInputStream gzi = new GZIPInputStream(entityStream, 65535)) {
            Method newBuilder = type.getMethod("newBuilder");
            GeneratedMessageV3.Builder<?> builder = (GeneratedMessageV3.Builder) newBuilder.invoke(type);
            return builder.mergeFrom(gzi).build();
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | IOException e) {
            throw new WebApplicationException(e, Status.INTERNAL_SERVER_ERROR);
        }
    }
}
