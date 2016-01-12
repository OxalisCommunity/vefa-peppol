/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.uprc.connector.emitterstub;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Jerry Dimitriou <jerouris@unipi.gr>
 */
public class MainStub {

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
     private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(MainStub.class);
    public static void main(String[] args) throws IOException {
        String sSBDHFolder = "SBDDrop";
        String sEvidenceFolder = "REMFolder";
        String sPolicyFile = "policy.xml";
        
        
        Path SBDHPath = Paths.get(sSBDHFolder);       
        try {
            Files.createDirectory(SBDHPath);
        } catch (IOException ex) {
        }
        
        Path evidencePath = Paths.get(sEvidenceFolder);       
        try {
            Files.createDirectory(evidencePath);
        } catch (IOException ex) {
        }
                
        String[] args2 = {sSBDHFolder};
        //Define the two threads
        DirWatcher dw = new DirWatcher(args2);
        EvidenceEmitterConsumer eec = new EvidenceEmitterConsumer();
        eec.setPath(evidencePath);
        eec.setPolicyFile(Paths.get(sPolicyFile));

        BlockingQueue<File> evidenceQueue = new LinkedBlockingQueue<>();

        // Link the two threads with a queue
        eec.setEvidenceQueue(evidenceQueue);
        eec.setPath(evidencePath);
        dw.setEvidenceQueue(evidenceQueue);

        //Run the threads
        Thread eect = new Thread(eec);
        eect.setName("Push-api-thread");
        eect.start();

        Thread dwt = new Thread(dw);
        dwt.setName("Rest-api-dir-thread");
        dwt.start();

    }

}
