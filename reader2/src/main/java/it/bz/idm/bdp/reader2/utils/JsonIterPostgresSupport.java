package it.bz.idm.bdp.reader2.utils;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.postgis.Point;

import com.jsoniter.JsonIterator;
import com.jsoniter.any.Any;
import com.jsoniter.output.JsonStream;
import com.jsoniter.spi.Decoder;
import com.jsoniter.spi.Encoder;
import com.jsoniter.spi.JsonException;
import com.jsoniter.spi.JsoniterSpi;

public class JsonIterPostgresSupport {

    private static boolean enabled;

    public static synchronized void enable() {
        if (JsonIterPostgresSupport.enabled) {
            throw new JsonException("JsonIterPostgresSupport.enable can only be called once");
        }
        enabled = true;
        JsoniterSpi.registerTypeEncoder(Point.class, new Encoder.ReflectionEncoder() {
            @Override
            public void encode(Object obj, JsonStream stream) throws IOException {
            	Point point = (Point)obj;
            	Map<String, Object> result = new HashMap<String, Object>();
            	result.put("srid", point.getSrid());
            	result.put("x", point.getX());
            	result.put("y", point.getY());
            	stream.writeVal(result);
            }

            @Override
            public Any wrap(Object obj) {
                return Any.wrap(enabled);
            }
        });
        JsoniterSpi.registerTypeDecoder(Point.class, new Decoder() {
            @Override
            public Object decode(JsonIterator iter) throws IOException {
            	try {
//            		return PGgeometry.geomFromString(iter.readString());
            		return new Point(iter.readString());
            	} catch (SQLException e) {
            		throw new JsonException(e);
            	}
            }
        });
    }
}
