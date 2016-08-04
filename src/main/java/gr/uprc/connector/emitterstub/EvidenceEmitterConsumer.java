/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.uprc.connector.emitterstub;

import eu.esens.abb.nonrep.EvidenceEmitter;
import eu.esens.abb.nonrep.EvidenceIssuerContext;
import eu.esens.abb.nonrep.ObligationHandler;
import eu.esens.abb.nonrep.TOElementException;
import eu.esens.abb.nonrep.Utilities;
import eu.esens.abb.nonrep.XACMLAttributes;
import eu.esens.abb.nonrep.XACMLRequestBuilder;
import eu.esens.abb.nonrep.eproc.FileSystemSBDMessageType;
import eu.esens.abb.nonrep.eproc.SBDMessageType;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

/**
 *
 * @author Jerry Dimitriou <jerouris@unipi.gr>
 */
public class EvidenceEmitterConsumer implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(EvidenceEmitterConsumer.class);
    public static final String DATATYPE_STRING = "http://www.w3.org/2001/XMLSchema#string";
    private BlockingQueue<File> evidenceQueue;
    private Path evidenceFolder;
    private Path policyFile;

    public void setEvidenceQueue(BlockingQueue<File> q) {
        this.evidenceQueue = q;
    }

    public void setPolicyFile(Path p) {
        LOG.debug("Policy File set to {}", p);
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

                LOG.info("Creating Evidence Issuer Security Context: Loading Certificate and Private Key");
                KeyStore ks = KeyStore.getInstance("JKS");
                ks.load(new FileInputStream("./s1.keystore"),
                        "spirit".toCharArray());
                X509Certificate cert = (X509Certificate) ks.getCertificate("server1");
                PrivateKey key = (PrivateKey) ks.getKey("server1", "spirit".toCharArray());
                EvidenceIssuerContext context = new EvidenceIssuerContext();
                context.setSigningKey(key);
                context.setIssuerCertificate(cert);

                //Evidence Emitter Instantiation
                LOG.info("Initiating Evidence Emitter Policy");
                EvidenceEmitter em = new EvidenceEmitter(policyFile);

                //This should be through Message Inspector, but for now it is ok to use the MessageType directly
                LOG.info("Creating Message Inspector for file");
                SBDMessageType sbd = new FileSystemSBDMessageType(f.toPath());

                LOG.info("Creating XACML Request");
                Element request = createRequest(sbd);

                LOG.info("Requesting Obligations (evidences) for specific document");
                ObligationHandler oh = em.requestObligations(sbd, request, context);

                if (oh != null) {

                    LOG.info("Discharge: Request Evidence creation");
                    oh.discharge();
                    Path p = evidenceFolder.resolve("evidence_"+f.getName());

                    try (OutputStream out = new BufferedOutputStream(
                            Files.newOutputStream(p, CREATE, APPEND))) {
                        Utilities.serialize(oh.getEvidence().getDocumentElement(), out);
                        LOG.info("Created REM Evidence {}",p);
                    } catch (IOException x) {
                        System.err.println(x);
                    }
                    
                }

            } catch (Exception ex) {
                LOG.error("Could not load policy file {}", policyFile.toAbsolutePath(), ex);
            }

        }
    }

    public void setPath(Path evidencePath) {
        this.evidenceFolder = evidencePath;
    }

    private Element createRequest(SBDMessageType msg) throws URISyntaxException, TOElementException {
        LinkedList<XACMLAttributes> actionList = new LinkedList<>();
        XACMLAttributes action = new XACMLAttributes();
        action.setDataType(new URI(DATATYPE_STRING));
        action.setIdentifier(new URI(
                "urn:eSENS:documentType"));
        action.setValue(msg.getDocumentTypeId());
        actionList.add(action);

        XACMLAttributes action2 = new XACMLAttributes();
        action2.setDataType(new URI(DATATYPE_STRING));
        action2.setIdentifier(new URI(
                "urn:eSENS:action"));
        action2.setValue("reception");
        actionList.add(action2);

        Element request = new XACMLRequestBuilder()
                .actionAttributes(actionList)
                .buildAsElement();
        return request;
    }
}
