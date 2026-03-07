/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at https://www.bahmni.org/license/mplv2hd.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.fhir.mapper.model;

import ca.uhn.fhir.model.dstu2.composite.IdentifierDt;
import ca.uhn.fhir.model.dstu2.composite.ResourceReferenceDt;
import ca.uhn.fhir.model.dstu2.resource.Encounter;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

public class FHIREncounter {
    private Encounter encounter;

    public FHIREncounter(Encounter encounter) {
        this.encounter = encounter;
    }

    public Encounter getEncounter() {
        return encounter;
    }
    
    public List<ResourceReferenceDt> getParticipantReferences() {
        List<ResourceReferenceDt> participants = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(encounter.getParticipant())) {
            for (Encounter.Participant participant : encounter.getParticipant()) {
                participants.add(participant.getIndividual());
            }
        }
        return participants;
    }

    public ResourceReferenceDt getFirstParticipantReference() {
        return CollectionUtils.isNotEmpty(encounter.getParticipant()) ? encounter.getParticipantFirstRep().getIndividual() : null;
    }

    public String getId() {
        return encounter.getId().getValue();
    }

    public ResourceReferenceDt getPatient() {
        return encounter.getPatient();
    }

    public ResourceReferenceDt getServiceProvider() {
        return encounter.getServiceProvider();
    }

    public List<IdentifierDt> getIdentifier() {
        return encounter.getIdentifier();
    }
}
