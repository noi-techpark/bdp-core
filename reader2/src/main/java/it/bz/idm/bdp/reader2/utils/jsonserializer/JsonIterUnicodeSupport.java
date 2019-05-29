package it.bz.idm.bdp.reader2.utils.jsonserializer;

import java.io.IOException;

import com.jsoniter.JsonIterator;
import com.jsoniter.any.Any;
import com.jsoniter.output.JsonStream;
import com.jsoniter.spi.Decoder;
import com.jsoniter.spi.Encoder;
import com.jsoniter.spi.JsonException;
import com.jsoniter.spi.JsoniterSpi;

public class JsonIterUnicodeSupport {

	private static boolean enabled;

    public static synchronized void enable() {
        if (JsonIterUnicodeSupport.enabled) {
            throw new JsonException("JsonIterUnicodeSupport.enable can only be called once");
        }
        JsonIterUnicodeSupport.enabled = true;
        JsoniterSpi.registerTypeEncoder(String.class, new Encoder.ReflectionEncoder() {
            @Override
            public void encode(Object obj, JsonStream stream) throws IOException {
            	System.out.println(obj.toString() + ":" + obj.toString().getBytes("UTF-8") + ":" + new String(obj.toString().getBytes("UTF-8"), "UTF-8"));
                stream.writeVal(obj.toString());//new String(obj.toString().getBytes("UTF-8")));
            }

            @Override
            public Any wrap(Object obj) {
            	System.out.println("WRAP");
            	return Any.wrap(true);
            }
        });
        JsoniterSpi.registerTypeDecoder(String.class, new Decoder() {
            @Override
            public Object decode(JsonIterator iter) throws IOException {
            	System.out.println("DEC");
            	return iter.readString() + " DECODE";
            }
        });
    }
}
