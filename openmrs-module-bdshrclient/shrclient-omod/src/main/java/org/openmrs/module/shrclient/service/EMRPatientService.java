/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at https://www.bahmni.org/license/mplv2hd.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.shrclient.service;

import org.openmrs.module.shrclient.model.Patient;
import org.openmrs.serialization.SerializationException;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface EMRPatientService {
    public org.openmrs.Patient createOrUpdateEmrPatient(Patient mciPatient);

    public org.openmrs.Patient getEMRPatientByHealthId(String healthId);

    public void savePatient(org.openmrs.Patient emrPatient);

    public void mergePatients(org.openmrs.Patient toBeRetainedPatient, org.openmrs.Patient toBeRetiredPatient) throws SerializationException;
}

