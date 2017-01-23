package org.openmrs.healthStandard.converter.fhir.fhirModels;

import ca.uhn.fhir.model.api.annotation.Block;
import ca.uhn.fhir.model.api.annotation.Child;
import ca.uhn.fhir.model.api.annotation.Extension;
import ca.uhn.fhir.util.ElementUtil;
import org.hl7.fhir.dstu3.model.BackboneElement;
import org.hl7.fhir.dstu3.model.StringType;

import static org.openmrs.healthStandard.converter.fhir.FhirExtensionUrls.locationTagDescriptionUrl;
import static org.openmrs.healthStandard.converter.fhir.FhirExtensionUrls.locationTagNameUrl;
import static org.openmrs.healthStandard.converter.fhir.FhirExtensionUrls.locationTagUuidUrl;

@Block
public class FhirLocationTag extends BackboneElement {

    @Child(name = "uuid")
    @Extension(url = locationTagUuidUrl, definedLocally = false, isModifier = false)
    private StringType uuid;

    @Child(name = "name")
    @Extension(url = locationTagNameUrl, definedLocally = false, isModifier = false)
    private StringType name;

    @Child(name = "description")
    @Extension(url = locationTagDescriptionUrl, definedLocally = false, isModifier = false)
    private StringType description;

    public FhirLocationTag() {
    }

    public FhirLocationTag(String uuid, String name, String description) {

        this.uuid = new StringType(uuid);
        this.name = new StringType(name);
        this.description = new StringType(description);
    }

    @Override
    public FhirLocationTag copy() {
        FhirLocationTag copy = new FhirLocationTag(uuid.getValue(), name.getValue(), description.getValue());
        return copy;
    }

    @Override
    public boolean isEmpty() {
        return super.isEmpty() && ElementUtil.isEmpty(name, description);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FhirLocationTag that = (FhirLocationTag) o;

        if (!uuid.getValueNotNull().equals(that.uuid.getValueNotNull())) return false;
        if (!name.getValueNotNull().equals(that.name.getValueNotNull())) return false;
        return description.getValueNotNull().equals(that.description.getValueNotNull());
    }

    @Override
    public int hashCode() {
        int result = uuid != null ? uuid.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        return result;
    }
}
