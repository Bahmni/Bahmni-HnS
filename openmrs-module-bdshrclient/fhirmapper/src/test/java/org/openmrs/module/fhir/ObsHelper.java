/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at https://www.bahmni.org/license/mplv2hd.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.fhir;

import org.openmrs.Obs;
import org.openmrs.module.fhir.mapper.model.CompoundObservation;

public class ObsHelper {
    public Obs findMemberObsByConceptName(Obs observation, String conceptName) {
        CompoundObservation compoundObservation = new CompoundObservation(observation);
        return compoundObservation.getMemberObsForConceptName(conceptName);
    }
}
