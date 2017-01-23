package org.openmrs.healthStandard.converter.fhir.fhirModels;

import ca.uhn.fhir.model.api.annotation.Child;
import ca.uhn.fhir.model.api.annotation.Extension;
import ca.uhn.fhir.model.api.annotation.ResourceDef;
import ca.uhn.fhir.util.ElementUtil;
import org.hl7.fhir.dstu3.model.Location;

import java.util.List;

import static org.openmrs.healthStandard.converter.fhir.FhirExtensionUrls.locationTagUrl;

@ResourceDef(name = "Location")
public class FhirLocation extends Location {

    @Child(name = "fhirLocationTag")
    @Extension(url = locationTagUrl, definedLocally = false, isModifier = false)
    protected List<FhirLocationTag> fhirLocationTag;

    public List<FhirLocationTag> getFhirLocationTag() {
        return fhirLocationTag;
    }

    public void setFhirLocationTag(List<FhirLocationTag> theFhirLocationTag) {
        fhirLocationTag = theFhirLocationTag;
    }

    @Override
    public boolean isEmpty() {
        return super.isEmpty() && ElementUtil.isEmpty(fhirLocationTag);
    }

}
