package cassandra;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;

public class CassandraConnector {


    private static Cluster cluster;
    private static Session session;


    public static Session connect(String keyspace){
        Cluster.Builder builder  = Cluster.builder().addContactPoint("127.0.0.1");
//        builder.withPort(9042);

        cluster = builder.build();

        if(keyspace!=null) session = cluster.connect(keyspace);
        else session = cluster.connect();

        return session;
    }

    public Session getSession() {
        return session;
    }

    public void close(){
        session.close();
        cluster.close();
    }
}
