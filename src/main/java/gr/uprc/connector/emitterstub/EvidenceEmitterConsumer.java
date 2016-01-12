/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.uprc.connector.emitterstub;

import eu.esens.abb.nonrep.EvidenceEmitter;
import java.io.File;
import java.nio.file.Path;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Jerry Dimitriou <jerouris@unipi.gr>
 */
public class EvidenceEmitterConsumer implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(EvidenceEmitterConsumer.class);
    private BlockingQueue<File> evidenceQueue;
    private Path evidenceFolder;
    private Path policyFile;

    public void setEvidenceQueue(BlockingQueue<File> q) {
        this.evidenceQueue = q;
    }
    
    public void setPolicyFile(Path p) {
        LOG.debug("Policy File set to {}",p);
        this.policyFile = p;
    }

    @Override
    public void run() {
        LOG.info("Started Evidence Creation Thread");
        while (true) {
            try {
                //find all the messages that are not read and have push to true
                File f;
                try {
                    f = evidenceQueue.take();
                } catch (InterruptedException x) {
                    return;
                }
                
                LOG.info("New File Received for Evidence Creation. Filename: {}", f.getName());
                EvidenceEmitter em = new EvidenceEmitter(policyFile);
                
            } catch (Exception ex) {
                LOG.error("Could not load policy file {}",policyFile);
            }
            
        }
    }

    void setPath(Path evidencePath) {
        this.evidenceFolder = evidencePath;
        
    }
}
