package rm.titansdata.web;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import org.locationtech.jts.geom.Point;

/**
 *
 * @author Ricardo Marquez
 */
public class PointTypeAdapter extends TypeAdapter<Point> {

  @Override
  public void write(JsonWriter writer, Point t) throws IOException {
    writer.beginObject();
    if (t != null) {
      writer.name("x").value(t.getX());
      writer.name("y").value(t.getY());
      writer.name("srid").value(t.getSRID());
    }
    writer.endObject();
    writer.flush();
  }

  @Override
  public Point read(JsonReader reader) throws IOException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

}
