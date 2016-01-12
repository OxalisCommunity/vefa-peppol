/*
 * Copyright (C) 2015 University Of Piraeus Research Center (UPRC)
 *
 *  Licensed under the MPL 1.1 or EUPL 1.2
 *
 *  The contents of this file are subject to the Mozilla Public License Version
 *  1.1 (the "License"); you may not use this file except in compliance with
 *  the License. You may obtain a copy of the License at:
 *  http://www.mozilla.org/MPL/
 *
 *  Software distributed under the License is distributed on an "AS IS" basis,
 *  WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 *  for the specific language governing rights and limitations under the
 *  License.
 *
 *  Alternatively, the contents of this file may be used under the
 *  terms of the EUPL, Version 1.2 or - as soon they will be approved
 *  by the European Commission - subsequent versions of the EUPL
 *  (the "License"); You may not use this work except in compliance
 *  with the License.
 *  You may obtain a copy of the License at:
 *  http://joinup.ec.europa.eu/software/page/eupl/licence-eupl
 */

package gr.uprc.connector.emitterstub;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import no.difi.vefa.sbdh.SbdhParser;
import no.difi.vefa.sbdh.SbdhParserFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.unece.cefact.namespaces.standardbusinessdocumentheader.StandardBusinessDocumentHeader;

/**
 *
 * @author Jerry Dimitriou <jerouris@unipi.gr>
 */
public class DirWatcher implements Runnable {

    private final Map<WatchKey, Path> keys;

    private final static Logger LOG = LoggerFactory.getLogger(DirWatcher.class);
    private final WatchService watcher;
    private final String[] initialPaths;
    private BlockingQueue pushQueue = null;
    private BlockingQueue evidenceQueue = null;

    @SuppressWarnings("unchecked")
    static <T> WatchEvent<T> cast(WatchEvent<?> event) {
        return (WatchEvent<T>) event;
    }

    public DirWatcher(String[] paths) throws IOException {
        initialPaths = paths;
        watcher = FileSystems.getDefault().newWatchService();
        keys = new HashMap<>();
        for (String path : paths) {
            Path dir = Paths.get(path);
            WatchKey key = dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE);
            keys.put(key, dir);
        }
    }
    
    public void setPushQueue(BlockingQueue q) {
        this.pushQueue = q;
    }
    
    public void setEvidenceQueue(BlockingQueue q) {
        this.evidenceQueue = q;
    }

    public void fullScan() {
        for (String path : initialPaths) {

            LOG.info("Rescanning Dir {} for missing files ...", path);
            for (File f : Paths.get(path).toFile().listFiles()) {
                checkAndAddFile(f);
            }
        }
    }

    @Override
    public void run() {
        
        LOG.info("Starting Scanner Thread");
        while (true) {
            // wait for key to be signalled
            WatchKey key;
            try {
                key = watcher.take();
            } catch (InterruptedException x) {
                return;
            }
            
            

            Path dir = keys.get(key);
            if (dir == null) {
                System.err.println("WatchKey not recognized!!");
                continue;
            }

            key.pollEvents().stream().forEach((event) -> {
                // Context for directory entry event is the file name of entry
                WatchEvent<Path> ev = cast(event);
                Path name = ev.context();
                Path child = dir.resolve(name);

                switch (event.kind().name()) {
                    case "ENTRY_CREATE":
                        checkAndAddFile(child.toFile());
                        break;
                }
            });

            // reset key and remove from set if directory no longer accessible
            boolean valid = key.reset();
            if (!valid) {
                keys.remove(key);

                // all directories are inaccessible
                if (keys.isEmpty()) {
                    break;
                }
            }
        }
    }

    private void checkAndAddFile(File f) {
        //Check if the file is a valid SBD
        LOG.info("Found new File: {}", f.getName());
        
        if (f.isFile()) {
            try {
                LOG.debug("Checking if the file is an SBD with a proper SBDH");
                StandardBusinessDocumentHeader doc;
                SbdhParser parser = SbdhParserFactory.sbdhParserAndExtractor();
                InputStream is = new FileInputStream(f);
                doc = parser.parse(is);
                is.close();
                if (doc != null) {
                    
                    //do other stuff and then put to evidence inspection queue
                    LOG.debug("File is a proper SBDH, adding to evidence queue");
                    if (evidenceQueue != null)
                        evidenceQueue.add(f);
               }

                 else {
                    //TODO Move to another not monitored directory
                    LOG.warn("Received file {} does not appear to be a valid SBD.", f.getName());
                }
            } catch (FileNotFoundException ex) {
                java.util.logging.Logger.getLogger(DirWatcher.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                java.util.logging.Logger.getLogger(DirWatcher.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
   
}
