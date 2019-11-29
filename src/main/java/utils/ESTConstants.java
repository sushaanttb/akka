package utils;

public class ESTConstants {

    public static final String CREATE_EST_PURCHASES_TABLE = " create table estpurchases ( " +
            " hhid  text primary key, cbpid  text, transactionid  text, equipmentid  text, equipmentname  text, " +
            " eventcode  text, eventtype  text, providerid  text, genre  text, chargeamount  text, purchasedatetime  text, " +
            " eventbillingdescription  text, commercialrate  text, facilitatorfee  text, eventstarttime  text, " +
            " eventendtime  text, ppvtype  text, packagedescription  text, eventrating  text, userid  text, " +
            " w3serviceprofileid  text, promotioncode  text, billingid  text, futureuse text);";

    public static final String CREATE_EST_LOG_TABLE = " create table est_log (filename varchar primary key, start_time timestamp, " +
            " end_time timestamp , status varchar , attempt_count int , error_details text); ";

    public static final String EST_KEYSPACE = "test";
    public static final String SOURCE_DIRECTORY = "tmp";
    public static final String ARCHIVE_DIRECTORY = "archive";

}
