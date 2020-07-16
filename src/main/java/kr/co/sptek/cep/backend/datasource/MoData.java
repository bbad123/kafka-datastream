package kr.co.sptek.cep.backend.datasource;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MoData implements TimestampAssignable<Long> {

  public long eventTime;
  public String eventKey;
  public String eventValue;
  public String sourceId;
  private Long ingestionTimestamp;

  private static transient DateTimeFormatter timeFormatter =
      DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
          .withLocale(Locale.US)
          .withZone(ZoneOffset.UTC);

  //	  public enum ValueType {
  //	    INT("INT"),
  //	    LONG("LONG");
  //
  //	    String representation;
  //
  //	    ValueType(String repr) {
  //	      this.representation = repr;
  //	    }
  //
  //	    public static ValueType fromString(String representation) {
  //	      for (ValueType b : ValueType.values()) {
  //	        if (b.representation.equals(representation)) {
  //	          return b;
  //	        }
  //	      }
  //	      return null;
  //	    }
  //	  }

  public static MoData fromString(String line) {
    List<String> tokens = Arrays.asList(line.split(","));
    int numArgs = 5;
    if (tokens.size() != numArgs) {
      throw new RuntimeException(
          "Invalid transaction: "
              + line
              + ". Required number of arguments: "
              + numArgs
              + " found "
              + tokens.size());
    }

    MoData moData = new MoData();

    try {
      Iterator<String> iter = tokens.iterator();
      moData.eventTime = ZonedDateTime.parse(iter.next(), timeFormatter).toInstant().toEpochMilli();
      moData.eventKey = iter.next();
      moData.eventValue = iter.next();
      moData.sourceId = iter.next();
      moData.ingestionTimestamp = Long.parseLong(iter.next());
    } catch (NumberFormatException nfe) {
      throw new RuntimeException("Invalid record: " + line, nfe);
    }

    return moData;
  }

  @Override
  public void assignIngestionTimestamp(Long timestamp) {
    this.ingestionTimestamp = timestamp;
  }
}
