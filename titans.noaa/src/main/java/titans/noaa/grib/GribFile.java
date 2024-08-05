package titans.noaa.grib;

import common.RmObjects;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.nio.file.Files;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang.mutable.MutableObject;
import titans.noaa.core.NoaaVariable;

/**
 *
 * @author Ricardo Marquez
 */
public class GribFile {

  public final ZonedDateTime datetimeref;
  public final int fcststep;
  public final NoaaVariable var;
  public final File grib;
  public final File gribIdx;

  /**
   *
   * @param rootFolder
   * @param var
   * @param subfolder
   * @param datetimeref
   * @param fcststep
   * @param filename
   * @return
   */
  public static GribFile create(File rootFolder, NoaaVariable var, // 
          int subfolder, ZonedDateTime datetimeref, int fcststep, String filename) {
    if (filename.contains("//") || filename.contains("\\")) {
      throw new RuntimeException("Invalid filename");
    }
    String datetext = datetimeref //
            .toOffsetDateTime() //
            .atZoneSameInstant(ZoneId.of("UTC")) //
            .format(new DateTimeFormatterBuilder()
                    .appendPattern("yyyy/MM/dd")
                    .toFormatter());
    File parent;
    if (subfolder >= 0) {
      parent = new File(rootFolder, String.format("%04d/%s", subfolder, datetext));
    } else {
      parent = new File(rootFolder, datetext);
    }
    File grib = new File(parent, filename);
    File gribIdx = new File(parent, filename + ".idx");
    GribFile result = new GribFile(datetimeref, fcststep, var, grib, gribIdx);
    return result;
  }

  /**
   *
   * @param datetimeref
   * @param fcststep
   * @param var
   * @param grib
   * @param gribIdx
   */
  private GribFile(ZonedDateTime datetimeref, int fcststep, // 
          NoaaVariable var, File grib, File gribIdx) {
    this.datetimeref = datetimeref;
    this.fcststep = fcststep;
    this.var = var;
    this.grib = grib;
    this.gribIdx = gribIdx;
  }

  /**
   *
   * @return
   */
  public String getBaseFileName() {
    return this.grib.getName();
  }

  /**
   *
   * @return
   */
  public boolean exists() {
    boolean result = this.grib.exists();
    return result;
  }

  /**
   *
   * @return
   */
  public OutputStream getOutputStream() {
    FileOutputStream result;
    try {
      MutableObject obj = new MutableObject();
      result = new FileOutputStream(this.grib) {
        @Override
        public void close() throws IOException {
          super.close();
          FileLock lock = (FileLock) obj.getValue();
          if (lock != null && lock.isValid()) {
            lock.close();
          }
        }
      };
      obj.setValue(result.getChannel().lock());
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
    return result;
  }

  /**
   *
   * @return
   */
  public boolean isNotLocked() {
    return !this.isLocked();
  }

  /**
   *
   * @return
   */
  public boolean isLocked() {
    try {
      if (this.notExists()) {
        return false;
      }
      
      try (FileOutputStream outputStream = new FileOutputStream(this.grib)) {
        FileChannel channel = outputStream.getChannel();
        FileLock lock;
        try {
          lock = channel.tryLock();
        } catch (OverlappingFileLockException ex) {
          return true;
        }
        if (lock == null) {
          return true;
        } else {
          lock.release();
          return false;
        }
      }

    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }

  /**
   *
   * @return
   */
  public boolean delete() {

    try {
      boolean result = Files.deleteIfExists(this.grib.toPath());
      Files.deleteIfExists(this.gribIdx.toPath());
      return result;
    } catch (Exception ex) {
      Logger.getAnonymousLogger().log(Level.WARNING,// 
              String.format("Deleting grib file '%s'", this.grib), ex);
    }
    return false;
  }

  /**
   *
   * @return
   */
  public boolean notExists() {
    return !this.grib.exists();
  }

  /**
   *
   * @return
   */
  @Override
  public int hashCode() {
    int hash = 7;
    hash = 41 * hash + Objects.hashCode(this.datetimeref);
    hash = 41 * hash + this.fcststep;
    return hash;
  }

  /**
   *
   * @param obj
   * @return
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final GribFile other = (GribFile) obj;
    if (this.fcststep != other.fcststep) {
      return false;
    }
    if (!Objects.equals(this.datetimeref, other.datetimeref)) {
      return false;
    }
    return true;
  }

  /**
   *
   * @return
   */
  @Override
  public String toString() {
    return "GribFile{" + "datetimeref=" + datetimeref + ", fcststep="
            + fcststep + ", var=" + var + ", grib=" + grib + ", gribIdx=" + gribIdx + '}';
  }

  /**
   *
   */
  public void createFile() {
    RmObjects.createFileIfDoesNotExists(grib.getParentFile());
    if (!this.grib.exists()) {
      try {
        this.grib.createNewFile();
      } catch (IOException ex) {
        throw new RuntimeException(ex);
      }
    }
  }
}
