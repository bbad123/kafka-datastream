package kr.co.sptek.cep.backend.datasource;

import java.util.SplittableRandom;
import org.springframework.stereotype.Component;

@Component
public class MoDataGenerator {

  private static String[] eventKeyArray = {
    "cpu usage", "memory usage", "disk usage", "network usage"
  };
  private static String[] sourceIdArray = {"host-1", "host-2", "host-3", "host-4"};
  private static int MAX_EVENT_KEY = 3;
  private static int MAX_EVENT_VALUE = 100;

  // private static double MIN_PAYMENT_AMOUNT = 5d;
  // private static double MAX_PAYMENT_AMOUNT = 20d;

  private SplittableRandom rnd;

  public MoDataGenerator(/*int maxRecordsPerSecond*/ ) {
    rnd = new SplittableRandom();
  }

  public MoData randomEvent(long id) {

    int keyIndx = rnd.nextInt(MAX_EVENT_KEY);
    String eventKey = eventKeyArray[keyIndx];

    String eventValue = Integer.toString(rnd.nextInt(MAX_EVENT_VALUE));

    int hostIndx = rnd.nextInt(MAX_EVENT_KEY);
    String sourceId = sourceIdArray[hostIndx];

    //	    long transactionId = rnd.nextLong(Long.MAX_VALUE);
    //	    long payeeId = rnd.nextLong(MAX_PAYEE_ID);
    //	    long beneficiaryId = rnd.nextLong(MAX_BENEFICIARY_ID);
    //	    double paymentAmountDouble =
    //	        ThreadLocalRandom.current().nextDouble(MIN_PAYMENT_AMOUNT, MAX_PAYMENT_AMOUNT);
    //	    paymentAmountDouble = Math.floor(paymentAmountDouble * 100) / 100;
    //	    BigDecimal paymentAmount = BigDecimal.valueOf(paymentAmountDouble);

    MoData moData =
        MoData.builder()
            .eventTime(System.currentTimeMillis())
            .eventKey(eventKey)
            .eventValue(eventValue)
            .sourceId(sourceId)
            .ingestionTimestamp(System.currentTimeMillis())
            .build();

    return moData;
  }

  private Transaction.PaymentType paymentType(long id) {
    int name = (int) (id % 2);
    switch (name) {
      case 0:
        return Transaction.PaymentType.CRD;
      case 1:
        return Transaction.PaymentType.CSH;
      default:
        throw new IllegalStateException("");
    }
  }
}
