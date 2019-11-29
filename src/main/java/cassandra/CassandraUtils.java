package cassandra;

import com.datastax.driver.core.Session;

public class CassandraUtils {

    public static String getCreateKeyspaceQuery(String keyspaceName){
        return "CREATE KEYSPACE "+ keyspaceName + " WITH replication = {'class':'SimpleStrategy', 'replication_factor':1};";
    }

    public static void executeQuery(String keyspace, String query){
        Session session = CassandraConnector.connect(null);

        if(session.getCluster().getMetadata().getKeyspace(keyspace) == null) {
            createKeyspace(keyspace);
            session = CassandraConnector.connect(keyspace);
        }

        session.execute("use " + keyspace);
        session.execute(query);
        session.close();
    }

    public static void createKeyspace(String keyspace){
        Session session = CassandraConnector.connect(null);
        session.execute(getCreateKeyspaceQuery(keyspace));
        System.out.println("Keyspace "+ keyspace +" created.");
        session.close();
    }

    public static void dropKeyspace(String keyspace){
        Session session = CassandraConnector.connect(null);

        if (session.getCluster().getMetadata().getKeyspace(keyspace) != null){
            //We are switching to System keyspace first before dropping that keyspace
            session.execute("drop keyspace "+ keyspace + ";");
            System.out.println("Keypspace "+ keyspace +" dropped! ");
        }else{
            System.out.println("Keypspace "+ keyspace + " doesn't exists.");
        }
        session.close();

    }

    public static void createTable(String keyspace, String tableDDL){
        CassandraUtils.executeQuery(keyspace, tableDDL);
        System.out.println("table created.");
    }

}
