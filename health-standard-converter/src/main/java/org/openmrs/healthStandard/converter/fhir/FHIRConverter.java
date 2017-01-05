package org.openmrs.healthStandard.converter.fhir;

import org.hl7.fhir.dstu3.model.DomainResource;
import org.openmrs.BaseOpenmrsObject;
import org.openmrs.OpenmrsObject;
import org.openmrs.healthStandard.converter.HealthStandardConverter;

public interface FHIRConverter<O extends OpenmrsObject, F extends DomainResource> extends HealthStandardConverter {

    public O toEMRResource(F fhirDomainResource);

    public F toFHIRResource(O emrResource);
}
