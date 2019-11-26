package cassandra;

import com.datastax.driver.core.Session;

public class CassandraUtils {

    public static String createKeyspaceQuery(String keyspaceName){
        return "CREATE KEYSPACE "+ keyspaceName + " WITH replication = {'class':'SimpleStrategy', 'replication_factor':1};";
    }

    public static void executeQuery(String keyspace, String query){
        Session session = CassandraConnector.connect(keyspace);

        if(keyspace!=null) session.execute("use " + keyspace);
        session.execute(query);
    }

    public static void drop(String keyspace){
        Session session = CassandraConnector.connect(null);

        if (session.getCluster().getMetadata().getKeyspace(keyspace) != null){
            CassandraUtils.executeQuery("system","drop keyspace "+ keyspace + ";");
            System.out.println(keyspace +" dropped! ");
        }

    }

    public static void createESTSchema(String keyspace){
        String keyspaceQuery = CassandraUtils.createKeyspaceQuery(keyspace);

        CassandraUtils.executeQuery(null,keyspaceQuery);
        System.out.println(keyspace + " created.");

        String createTableQuery = "create table estpurchases ( " +
                " hhid  text primary key, cbpid  text, transactionid  text, equipmentid  text, equipmentname  text, eventcode  text, eventtype  text, providerid  text, genre  text, chargeamount  text, purchasedatetime  text, eventbillingdescription  text, commercialrate  text, facilitatorfee  text, eventstarttime  text, eventendtime  text, ppvtype  text, packagedescription  text, eventrating  text, userid  text, w3serviceprofileid  text, promotioncode  text, billingid  text, \n" +
                " futureuse text); ";

        CassandraUtils.executeQuery(keyspace, createTableQuery);
        System.out.println(keyspace+ ":estpurchases created.");
    }
}
