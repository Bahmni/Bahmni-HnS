package org.openmrs.module.shrclient.scheduler.tasks;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openmrs.module.shrclient.handlers.EncounterPush;
import org.openmrs.module.shrclient.handlers.PatientPush;
import org.openmrs.module.shrclient.util.PropertiesReader;

import java.net.URISyntaxException;

public class BahmniSyncTask extends AbstractBahmniSyncTask {
    private static final Logger log = LogManager.getLogger(BahmniSyncTask.class);

    @Override
    protected void executeBahmniTask(PatientPush patientPush, EncounterPush encounterPush, PropertiesReader propertiesReader) {
        log.debug("SCHEDULED JOB : SHR Patient Sync Task");
        try {
            getFeedClient(OPENMRS_PATIENT_FEED_URI, patientPush, propertiesReader.getMciMaxFailedEvent()).processEvents();
            getFeedClient(OPENMRS_ENCOUNTER_FEED_URI, encounterPush, propertiesReader.getShrMaxFailedEvent()).processEvents();
        } catch (URISyntaxException e) {
            log.error(e.getMessage());
        }
    }
}
