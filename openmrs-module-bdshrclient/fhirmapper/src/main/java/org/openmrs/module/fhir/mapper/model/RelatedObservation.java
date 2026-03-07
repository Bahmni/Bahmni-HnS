/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at https://www.bahmni.org/license/mplv2hd.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.fhir.mapper.model;

import ca.uhn.fhir.model.dstu2.composite.ResourceReferenceDt;
import ca.uhn.fhir.model.dstu2.resource.Observation;
import ca.uhn.fhir.model.dstu2.valueset.ObservationRelationshipTypeEnum;
import org.openmrs.module.shrclient.util.SystemProperties;

public class RelatedObservation implements PartOf<Observation> {

    private Observation relatedObservation;

    public RelatedObservation(Observation relatedObservation) {
        this.relatedObservation = relatedObservation;
    }

    @Override
    public Observation mergeWith(Observation observation, SystemProperties systemProperties) {
        Observation.Related related = observation.addRelated();
        related.setTarget(new ResourceReferenceDt().setReference(relatedObservation.getId().getValue()));
        related.setType(ObservationRelationshipTypeEnum.HAS_MEMBER);
        return observation;
    }

}
