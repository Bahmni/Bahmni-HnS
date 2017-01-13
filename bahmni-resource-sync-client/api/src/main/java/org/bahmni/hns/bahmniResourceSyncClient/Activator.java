package org.bahmni.hns.bahmniResourceSyncClient;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.BaseModuleActivator;

public class Activator extends BaseModuleActivator {

    Log log = LogFactory.getLog(Activator.class);


    @Override
    public void started() {
        log.info("Started the Bahmni Resource Sync Atom Feed Client module");
    }

    @Override
    public void stopped() {
        log.info("Stopped the Bahmni Resource Sync Atom Feed Client module");
    }
}
