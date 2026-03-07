/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at https://www.bahmni.org/license/mplv2hd.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.shrclient.service;

import org.openmrs.Patient;
import org.openmrs.module.shrclient.web.controller.dto.EncounterEvent;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface EMREncounterService {
    public void createOrUpdateEncounters(Patient emrPatient, List<EncounterEvent> encounterEvents);
    public void createOrUpdateEncounter(Patient emrPatient, EncounterEvent encounterEvent) throws Exception;
}
