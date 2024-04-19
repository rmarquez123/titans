package rm.titansdata.web.rasters;

import common.RmTimer;
import common.geom.SridUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.measure.unit.Unit;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.locationtech.jts.io.WKTReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import rm.titansdata.Parameter;
import rm.titansdata.plugin.ClassType;
import rm.titansdata.plugin.Clazz;
import rm.titansdata.properties.Bounds;
import rm.titansdata.raster.Raster;
import rm.titansdata.raster.RasterCells;
import rm.titansdata.web.JsonConverterUtil;
import rm.titansdata.web.RequestParser;
import rm.titansdata.web.ResponseHelper;
import rm.titansdata.web.project.ProjectEntity;
import rm.titansdata.web.user.session.SessionManager;
import rm.titansdata.web.user.session.SessionScopedBean;
import titans.noaa.core.NoaaParameter;

/**
 *
 * @author Ricardo Marquez
 */
@Controller
public class RastersServlet {

  @Autowired
  private ResponseHelper responseHelper;

  @Autowired
  private RastersSourceService rastersSourceService;

  @Autowired
  private RastersValueService rastersValueService;

  @Autowired
  private RasterImageService rastersImageService;

  @Autowired
  private AbstractParameterFactory parameterFactory;

  @Autowired
  @Qualifier("user.project")
  private SessionScopedBean<ProjectEntity> projectEntity;

  @RequestMapping(path = "/getRasters",
          params = {"userId"},
          method = RequestMethod.GET
  )
  public void cleanup(HttpServletRequest req, HttpServletResponse response) {

  }

  /**
   *
   * @param req
   * @param response
   */
  @RequestMapping(path = "/getRasters",
          params = {
            "projectId", "dateTime"
          },
          method = RequestMethod.GET
  )
  public void getRasters(HttpServletRequest req, HttpServletResponse response) {
    RequestParser parser = new RequestParser(req);
    int projectId = parser.getInteger("projectId");
    ZonedDateTime zonedDateTime = parser.getZonedDateTime("dateTime", "UTC");
    rastersValueService.deleteStoredFilesBefore(projectId, zonedDateTime);
  }

  /**
   *
   * @param req
   * @param response
   */
  @RequestMapping(path = "/getRasterClasses",
          params = {"rasterId"},
          method = RequestMethod.GET
  )
  public void getRasterClasses(HttpServletRequest req, HttpServletResponse response) {
    RequestParser parser = new RequestParser(req);
    Long rasterId = parser.getLong("rasterId");
    RasterEntity raster = this.rastersSourceService.getRaster(rasterId);

    String sourceTitle = raster.sourceTitle;
    Map<ClassType, List<Clazz>> a = this.parameterFactory.getClasses(sourceTitle);
    JSONObject f = new JSONObject();
    a.forEach((k, v) -> {
      JSONArray jsonObj = new JSONArray();
      for (Clazz clazze : v) {
        jsonObj.put(clazze.toJson());
      }
      try {
        f.put(k.name(), jsonObj);
      } catch (JSONException ex) {
        throw new RuntimeException(ex);
      }
    });
    Map<String, Object> map = new HashMap<>();
    map.put("clazzes", f);
    this.responseHelper.send(map, response);
  }

  /**
   *
   * @param req
   * @param response
   */
  @RequestMapping(path = "/getRasterParameters", //
          method = RequestMethod.GET, //
          params = {"rasterId", "clazzes"
          })//
  public void getRasterParameters(HttpServletRequest req, HttpServletResponse response) {
    RequestParser parser = new RequestParser(req);
    Long rasterId = parser.getLong("rasterId");

    RasterEntity raster = this.rastersSourceService.getRaster(rasterId);
    String sourceTitle = raster.sourceTitle;
    JSONArray jsonArray;
    try {
      jsonArray = new JSONArray(req.getParameter("clazzes"));
    } catch (JSONException ex) {
      throw new RuntimeException(ex);
    }
    List<Clazz> clazzes = this.parameterFactory.getClasse(sourceTitle, jsonArray);
    List<JSONObject> params = this.parameterFactory.getParameters(sourceTitle, clazzes)
            .stream()
            .map(p -> p.toJSONObject())
            .collect(Collectors.toList());
    Map<String, Object> map = new HashMap<>();
    map.put("values", params);
    this.responseHelper.send(map, response);
  }

  /**
   *
   * @param values
   * @return
   * @throws RuntimeException
   */
  private JSONObject toJson(Map<RasterGroupEntity, List<Long>> values) {
    JSONObject obj = new JSONObject();
    JSONArray entries = new JSONArray();
    try {
      obj.put("rastergroups", entries);
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
    values.entrySet().forEach(e -> {
      RasterGroupEntity k = e.getKey();
      List<Long> rasterIds = e.getValue();
      JSONArray rasterIdsJson = new JSONArray();
      rasterIds.stream().forEach((rasterId) -> {
        try {
          rasterIdsJson.put(rasterIdsJson.length(), rasterId);
        } catch (Exception ex) {
          throw new RuntimeException(ex);
        }
      });
      try {
        JSONObject o = new JSONObject();
        o.put("name", k.name);
        o.put("rasterGroupId", k.rasterGroupId);
        o.put("rasterIds", rasterIdsJson);
        entries.put(entries.length(), o);
      } catch (Exception ex) {
        throw new RuntimeException(ex);
      }
    });
    return obj;
  }

  @RequestMapping(path = "/getRaster",
          params = "rasterId",
          method = RequestMethod.GET
  )
  public void getRaster(HttpServletRequest req, HttpServletResponse res) {
    RequestParser parser = new RequestParser(req);
    Long rasterId = parser.getLong("rasterId");
    Map<String, Object> map = new HashMap<>();
    RasterEntity value = this.rastersSourceService.getRaster(rasterId);
    map.put("value", value);
    this.responseHelper.send(map, res);
  }

  /**
   *
   * @param req
   * @param res
   */
  @RequestMapping(path = "/getRastersByGroupId",
          params = "rasterGroupId",
          method = RequestMethod.GET)
  public void getRastersByGroupId(HttpServletRequest req, HttpServletResponse res) {
    RequestParser parser = new RequestParser(req);
    Long rastersGroupId = parser.getLong("rasterGroupId");
    Map<String, Object> map = new HashMap<>();
    List<Long> values = this.rastersSourceService.getRastersByGroupId(rastersGroupId);
    map.put("value", values);
    this.responseHelper.send(map, res);
  }

  /**
   *
   * @param req
   * @param res
   */
  @RequestMapping(
          path = "/getRasterValues",
          params = {"rasterId", "geometry", "parameter", "srid"}, method = RequestMethod.GET)
  public void getRasterValues(HttpServletRequest req, HttpServletResponse res) {
    RequestParser parser = new RequestParser(req);
    Long rasterId = parser.getLong("rasterId");
    int srid = parser.getInteger("srid");
    Geometry geometry = parser.parseGeometry("geometry", srid);
    if (srid == 4326) {
      geometry = SridUtils.transform(geometry, 3857);
    }
    JSONObject jsonObject = this.getParameterJson(parser);
    Parameter param = parameterFactory.get(jsonObject);
    int projectId = this.getProjectId();
    Bounds bounds = this.getBoundsFromProject();
    RasterCells values = this.rastersValueService
            .getRasterValues(rasterId, projectId, param, geometry, bounds);
    RmTimer timer = RmTimer.start();
    String result = JsonConverterUtil.toJson(values);
    this.responseHelper.sendAsZippedFile(result, res);
    timer.endAndPrint("sending");
  }

  /**
   *
   * @param req
   * @param res
   */
  @RequestMapping(
          path = "/getBaseRasterValues",
          params = {"rasterId", "geometry", "parameters", "srid"}, method = RequestMethod.GET)
  public void getBaseRasterValues(HttpServletRequest req, HttpServletResponse res) {
    RequestParser parser = new RequestParser(req);
    Long rasterId = parser.getLong("rasterId");
    int srid = parser.getInteger("srid");
    Geometry geometry = parser.parseGeometry("geometry", srid);
    JSONArray jsonArr = this.getParametersJsonArray(parser);
    List<Parameter> params = new ArrayList<>();
    for (int i = 0; i < jsonArr.length(); i++) {
      try {
        JSONObject jsonObject = jsonArr.getJSONObject(i);
        Parameter param = parameterFactory.get(jsonObject);
        params.add(param);
      } catch (Exception ex) {
        throw new RuntimeException(ex);
      }
    }
    int projectId = this.getProjectId();
    geometry.getSRID();
    Parameter firstParam = params.get(0);
    List<Point> points = this.getRasterPoints(rasterId, projectId, firstParam, geometry);
    Map<Integer, Point> ps = new HashMap<>(points.size());
    for (int i = 0; i < points.size(); i++) {
      ps.put(i, points.get(i));
    }
    Map<Integer, PointValues> pointValues = new HashMap<>(points.size());
    for (int paramIndex = 0; paramIndex < params.size(); paramIndex++) {
      Parameter param = params.get(paramIndex);
      Map<Integer, Double> values = this.rastersValueService //
              .getRasterValue(rasterId, projectId, param, ps);

      for (Map.Entry<Integer, Double> entry : values.entrySet()) {
        Integer pointIndex = entry.getKey();
        if (paramIndex == 0) {
          Point point = ps.get(pointIndex);
          pointValues.putIfAbsent(pointIndex, new PointValues(point));
        }
        pointValues.get(pointIndex).values.add(entry.getValue());
      }
    }
    RmTimer timer = RmTimer.start();
    String result = JsonConverterUtil.toJson(pointValues);
    this.responseHelper.sendAsZippedFile(result, res);
    timer.endAndPrint("sending");
  }

  public static class PointValues {

    final Point point;
    final List<Double> values = new ArrayList<>();

    public PointValues(Point point) {
      this.point = point;
    }
  }

  /**
   *
   * @param req
   * @param res
   */
  @RequestMapping(
          path = "/preload",
          params = {"rasterId", "geometry", "parameter", "srid"}, method = RequestMethod.GET)
  public void preload(HttpServletRequest req, HttpServletResponse res) {
    RequestParser parser = new RequestParser(req);
    Long rasterId = parser.getLong("rasterId");
    int srid = parser.getInteger("srid");
    Geometry geometry = parser.parseGeometry("geometry", srid);
    if (srid == 4326) {
      geometry = SridUtils.transform(geometry, 3857);
    }
    JSONObject jsonObject = this.getParameterJson(parser);
    Parameter param = parameterFactory.get(jsonObject);

    int projectId = this.getProjectId();
    Bounds bounds = this.getBoundsFromProject();
    this.rastersValueService
            .getRasterValues(rasterId, projectId, param, geometry, bounds);
    Map<String, Object> map = new HashMap<>();
    this.responseHelper.send(map, res);
  }

  /**
   *
   * @param req
   * @param res
   */
  @RequestMapping(
          path = "/getRasterValue",
          params = {"rasterId", "point", "parameter", "srid"},
          method = RequestMethod.GET
  )
  public void getRasterValue(HttpServletRequest req, HttpServletResponse res) {
    RequestParser parser = new RequestParser(req);
    long rasterId = parser.getLong("rasterId");
    int srid = parser.getInteger("srid");
    Point point = (Point) parser.parseGeometry("point", srid);
    JSONObject jsonObject = this.getParameterJson(parser);
    Parameter param = parameterFactory.get(jsonObject);
    Map<String, Object> map = new HashMap<>();
    int projectId = this.getProjectId();
    double value = this.rastersValueService.getRasterValue(rasterId, projectId, param, point);
    map.put("value", value);
    this.responseHelper.send(map, res);
  }

  /**
   *
   * @param req
   * @param res
   */
  @RequestMapping(
          path = "/getRasterPointValues",
          params = {"rasterId", "point", "parameters", "srid"},
          method = RequestMethod.GET
  )
  public void getRasterPointValues(HttpServletRequest req, HttpServletResponse res) {
    RequestParser parser = new RequestParser(req);
    long rasterId = parser.getLong("rasterId");
    int srid = parser.getInteger("srid");
    Point point = (Point) parser.parseGeometry("point", srid);
    if (srid == 4326) {
      point = SridUtils.transform(point, 3857);
    }
    JSONArray jsonObject = this.getParametersJsonArray(parser);
    List<Parameter> param = this.parameterFactory.get(jsonObject);
    Map<String, Object> map = new HashMap<>();
    int projectId = this.getProjectId();
    Map<Parameter, Double> values = this.rastersValueService //
            .getPointRasterValues(rasterId, projectId, param, point);
    Map<String, Double> s = values.entrySet().stream()
            .collect(Collectors.toMap(d -> d.getKey().getKey(), d -> d.getValue()));
    map.put("values", s);
    this.responseHelper.send(map, res);
  }

  /**
   *
   * @param req
   * @param res
   */
  @RequestMapping(
          path = "/getRasterPointListValues",
          params = {"rasterId", "points", "parameter", "srid"},
          method = RequestMethod.GET)
  public void getRasterPointListValues(HttpServletRequest req, HttpServletResponse res) {
    RequestParser parser = new RequestParser(req);
    long rasterId = parser.getLong("rasterId");
    int srid = parser.getInteger("srid");
    Map<Integer, Point> points = parseParameterPoints(parser, srid);
    JSONObject jsonObject = this.getParameterJson(parser);
    Parameter param = this.parameterFactory.get(jsonObject);
    Map<String, Object> map = new HashMap<>();
    int projectId = this.getProjectId();
    Map<Integer, Double> values = this.rastersValueService //
            .getRasterValue(rasterId, projectId, param, points);
    map.put("values", values);
    this.responseHelper.send(map, res);
  }

  /**
   *
   * @param parser
   * @param srid
   * @return
   * @throws RuntimeException
   */
  private Map<Integer, Point> parseParameterPoints(RequestParser parser, int srid) {
    Map<Integer, Point> points = new HashMap<>();
    try {
      JSONArray pointsjson = new JSONArray(parser.getString("points"));
      PrecisionModel.Type type = PrecisionModel.FLOATING;
      GeometryFactory factory = new GeometryFactory(new PrecisionModel(type), srid);
      WKTReader reader = new WKTReader(factory);
      for (int i = 0; i < pointsjson.length(); i++) {
        JSONObject p = pointsjson.getJSONObject(i);
        int pointId = p.getInt("id");
        Point point = (Point) reader.read(p.getString("point"));
        points.put(pointId, point);
      }
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
    return points;
  }

  /**
   *
   * @param parser
   * @return
   */
  private JSONObject getParameterJson(RequestParser parser) {
    JSONObject result;
    try {
      String string = parser.getString("parameter");
      result = new JSONObject(string);
    } catch (JSONException ex) {
      throw new RuntimeException(ex);
    }
    return result;
  }

  /**
   *
   * @param parser
   * @return
   */
  private JSONArray getParametersJsonArray(RequestParser parser) {
    JSONArray result;
    try {
      String string = parser.getString("parameters");
      result = new JSONArray(string);
    } catch (JSONException ex) {
      throw new RuntimeException(ex);
    }
    return result;
  }

  @RequestMapping(
          path = "/getNoaaRasterImage",
          params = {"rasterId", "variable", "datetime", "zoneId"},
          method = RequestMethod.GET
  )
  public void getNoaaRasterImage(HttpServletRequest req, HttpServletResponse res) {
    RequestParser parser = new RequestParser(req);
    long rasterId = parser.getLong("rasterId");
    String noaaVar = parser.getString("variable");

    ZonedDateTime datetime = parser.getZonedDateTime("datetime", "zoneId");
    String parentKey = this.rastersSourceService.getRaster(rasterId).sourceTitle;
    Parameter param = new NoaaParameter(parentKey, datetime, 0, noaaVar, Unit.ONE);
    Bounds bounds = this.getBoundsFromProject();
    int projectId = this.getProjectId();
    String colorMap = "Viridis";
    RasterImageResult image = this.rastersImageService.getRasterImage( //
            rasterId, projectId, param, bounds, colorMap);
    Map<String, Object> map = new HashMap<>();
    map.put("value", image);
    this.responseHelper.send(map, res);
  }

  /**
   *
   * @param req
   * @param res
   */
  @RequestMapping(
          path = "/getRasterImage",
          params = {"rasterId", "parameter", "colorMap"},
          method = RequestMethod.GET
  )
  public void getRasterImage(HttpServletRequest req, HttpServletResponse res) {
    RequestParser parser = new RequestParser(req);
    long rasterId = parser.getLong("rasterId");
    JSONObject jsonObject = this.getParameterJson(parser);
    Parameter param = parameterFactory.get(jsonObject);
    Bounds bounds = this.getBoundsFromProject();
    int projectId = this.getProjectId();
    String colorMap = parser.getString("colorMap");
    RasterImageResult image = this.rastersImageService.getRasterImage//
            (rasterId, projectId, param, bounds, colorMap);
    Map<String, Object> map = new HashMap<>();
    map.put("value", image);
    this.responseHelper.send(map, res);
  }

  @Autowired
  private ImageDirectoryService directoryService;

  /**
   *
   * @param req
   * @param res
   */
  @RequestMapping(
          path = "/data",
          params = {"code"},
          method = RequestMethod.GET
  )
  public void data(HttpServletRequest req, HttpServletResponse res) {
    RequestParser parser = new RequestParser(req);
    String code = parser.getString("code");
    File f = this.directoryService.getImageFile(code);
    try {
      String contentType = "image/png";
      res.setContentType(contentType);
      res.setContentLength((int) f.length());
      res.setHeader("Content-Disposition", "inline; filename=\"" + f.getName() + "\"");
      try (OutputStream out = res.getOutputStream(); FileInputStream in = new FileInputStream(f)) {
        byte[] buffer = new byte[4096];
        int bytesRead;
        while ((bytesRead = in.read(buffer)) != -1) {
          out.write(buffer, 0, bytesRead);
        }
      }
    } catch (IOException e) {
      res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
  }

  /**
   *
   * @return
   */
  private Bounds getBoundsFromProject() {
    String authToken = SessionManager.getSessionAuthToken();
    ProjectEntity project = this.projectEntity.getValue(authToken);
    Bounds result = project.getBounds();
    result = result.transform(3857);
    return result;
  }

  /**
   *
   * @return
   */
  private int getProjectId() {
    String authToken = SessionManager.getSessionAuthToken();
    ProjectEntity proj = this.projectEntity.getValue(authToken);
    int result = proj.projectId;
    return result;
  }

  /**
   *
   * @param req
   * @param res
   */
  @RequestMapping(
          path = "/getRasterPoints",
          params = {"rasterId", "geometry", "parameter", "srid"},
          method = RequestMethod.GET
  )
  public void getRasterPoints(HttpServletRequest req, HttpServletResponse res) {
    RequestParser parser = new RequestParser(req);
    Long rasterId = parser.getLong("rasterId");
    JSONObject jsonObject = this.getParameterJson(parser);
    Parameter param = parameterFactory.get(jsonObject);
    int projectId = this.getProjectId();
    int srid = parser.getInteger("srid");
    Geometry geometry = parser.parseGeometry("geometry", srid);
    List<Point> points = this.getRasterPoints(rasterId, projectId, param, geometry);
    Map<String, Object> map = new HashMap<>();
    map.put("value", points);
    this.responseHelper.send(map, res);
  }

  private List<Point> getRasterPoints(Long rasterId, int projectId, Parameter param, Geometry geometry) {
    Raster s = this.rastersValueService.getRaster(rasterId, projectId, param);
    List<Point> points;
    if (geometry instanceof LinearRing) {
      points = s.getPoints((LinearRing) geometry);
    } else if (geometry instanceof LineString) {
      points = s.getPoints((LineString) geometry);
    } else {
      throw new UnsupportedOperationException("Geometry is not a linear ring or line string");
    }
    return points;
  }
}
