package actors;

import akka.actor.AbstractActor;
import akka.actor.Props;
import cassandra.CassandraUtils;
import utils.ESTConstants;

//will dump ESTObject to ESTPurchases Table
public class CassandraActor extends AbstractActor {

    static public Props props(){
        return Props.create(CassandraActor.class, CassandraActor::new);
    }

    @Override
    public Receive createReceive(){
        return receiveBuilder().match(ESTLogMessage.class, estLogMessage -> {
            System.out.println("In cassandra actor, estLogMessage ("+estLogMessage.status +") received for :: "+ estLogMessage.fileName + " by " + this.self() + "from ::" + this.sender());
            logEST(estLogMessage);
        })
        .build();
    }

    public static void logEST(CassandraActor.ESTLogMessage estLogMessage){

        String query="";

        if(estLogMessage.getStatus().equals(CassandraActor.ESTLogStatus.CREATED)){
            query= "INSERT INTO est_log (filename,start_time,status) VALUES ( '"+ estLogMessage.getFileName() + "' , '" + java.time.LocalDateTime.now()
                    + "' , '" +estLogMessage.getStatus()+"') ";
        }else if(estLogMessage.getStatus().equals(CassandraActor.ESTLogStatus.ARCHIVED)){
            query= "UPDATE est_log set end_time= '"+  java.time.LocalDateTime.now()+ "' ,status = '"+ estLogMessage.getStatus() +"' where filename = '"+estLogMessage.getFileName() + "'";
        }

        if(query!="") CassandraUtils.executeQuery(ESTConstants.EST_KEYSPACE,query);
    }


    public static class ESTPurchaseMessage{
        //hhid
        //cbpid ..
    }

    public static enum ESTLogStatus{
         CREATED,ERROR,ARCHIVED;
    }

    public static class ESTLogMessage {
        private String fileName;
        private ESTLogStatus status;
        private String errorDetails;

        public ESTLogMessage(String fileName,ESTLogStatus estLogStatus) {
            this.fileName = fileName;
            this.status = estLogStatus;
        }

        public ESTLogMessage(String fileName, ESTLogStatus estLogStatus, String errorDetails) {
            this.fileName = fileName;
            this.status = estLogStatus;
            this.errorDetails = errorDetails;
        }

        public String getFileName() {
            return fileName;
        }

        public ESTLogStatus getStatus() {
            return status;
        }

        public String getErrorDetails() {
            return errorDetails;
        }
    }

}
