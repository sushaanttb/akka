package cassandra;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;

public class CassandraConnector {

    private static Cluster cluster = Cluster.builder().addContactPoint("127.0.0.1").withPort(9042).build();
    private static Session session;


    public static Session connect(String keyspace){
        if(keyspace!=null) session = cluster.connect(keyspace);
        else session = cluster.connect();

        return session;
    }

    public static Session getSession() {
        return session;
    }

    public static void closeSession(){ session.close();}

    public static void closeCluster(){ cluster.close();}
}
