package actors;

import akka.actor.AbstractActor;
import akka.actor.Props;

public class LineActor extends AbstractActor {
    private int totalnumOfLines;

    private int currentLineNum;

    public LineActor(int totalnumOfLines) {
        this.totalnumOfLines = totalnumOfLines;
    }

    static public Props props(int totalnumOfLines){
        return Props.create(LineActor.class, ()-> new LineActor(totalnumOfLines));
    }

    @Override
    public Receive createReceive(){
        return receiveBuilder().match(LineMessage.class, lineMessage -> {
            currentLineNum++;
            System.out.println("Line Processing STARTED for :: "+ lineMessage.fileName + "::"+ lineMessage.line + " by "+ this.self() + "from ::" + this.sender() );
            //Read file and dump to cassandra
            //read file as per Business logic, map line to ESTObject and send it to CassandraActor
            System.out.println("Line Processing COMPLETED for :: "+ lineMessage.fileName + "::"+  lineMessage.line +" by "+ this.self() + "from ::" + this.sender() );

            //convey to fileActor i.e. it's parent that all lines completed
            if(currentLineNum==totalnumOfLines) this.sender().tell( new FileActor.FileCompletionMessage(lineMessage.fileName),getSelf());
            
        }).build();
    }

    static public class LineMessage {
        private final String fileName;
        private  final String line;
        public LineMessage(String fileName, String line) { this.line = line; this.fileName = fileName;}
    }
}
