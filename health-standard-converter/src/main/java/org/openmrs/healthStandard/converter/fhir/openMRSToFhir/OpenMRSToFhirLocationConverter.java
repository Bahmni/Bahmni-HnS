package org.openmrs.healthStandard.converter.fhir.openMRSToFhir;

import org.hl7.fhir.dstu3.model.Address;
import org.hl7.fhir.dstu3.model.Location;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.StringType;
import org.openmrs.LocationTag;
import org.openmrs.healthStandard.converter.fhir.FHIRConstants;
import org.openmrs.healthStandard.converter.fhir.FHIRConverter;
import org.openmrs.healthStandard.converter.fhir.fhirModels.FhirLocation;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class OpenMRSToFhirLocationConverter implements FHIRConverter<org.openmrs.Location, FhirLocation> {

    @Override
    public FhirLocation convert(org.openmrs.Location omrsLocation) {
        FhirLocation fhirLocation = new FhirLocation();

        fhirLocation.setId(omrsLocation.getUuid());
        fhirLocation.setName(omrsLocation.getName());
        fhirLocation.setDescription(omrsLocation.getDescription());

        Address address = new Address();
        address.setCity(omrsLocation.getCityVillage());
        address.setDistrict(omrsLocation.getCountyDistrict());
        address.setState(omrsLocation.getStateProvince());
        address.setCountry(omrsLocation.getCountry());
        address.setPostalCode(omrsLocation.getPostalCode());
        List<StringType> addressStrings = new ArrayList<>();
        addressStrings.add(new StringType(omrsLocation.getAddress1()));
        addressStrings.add(new StringType(omrsLocation.getAddress2()));
        addressStrings.add(new StringType(omrsLocation.getAddress3()));
        addressStrings.add(new StringType(omrsLocation.getAddress4()));
        addressStrings.add(new StringType(omrsLocation.getAddress5()));
        address.setLine(addressStrings);
        address.setUse(Address.AddressUse.WORK);
        fhirLocation.setAddress(address);

        Location.LocationPositionComponent position = fhirLocation.getPosition();
        if (omrsLocation.getLongitude() != null && !omrsLocation.getLongitude().isEmpty()) {
            BigDecimal longitude = new BigDecimal(omrsLocation.getLongitude());
            position.setLongitude(longitude);
        }

        if (omrsLocation.getLatitude() != null && !omrsLocation.getLatitude().isEmpty()) {
            BigDecimal latitude = new BigDecimal(omrsLocation.getLatitude());
            position.setLatitude(latitude);
        }

        if (!omrsLocation.getRetired()) {
            fhirLocation.setStatus(Location.LocationStatus.ACTIVE);
        } else {
            fhirLocation.setStatus(Location.LocationStatus.INACTIVE);
        }

        if (omrsLocation.getParentLocation() != null) {
            Reference parent = new Reference();
            parent.setDisplay(omrsLocation.getParentLocation().getName());
            parent.setReference(FHIRConstants.LOCATION + "/" + omrsLocation.getParentLocation().getUuid());
            fhirLocation.setPartOf(parent);
        }

        Set<LocationTag> tags = omrsLocation.getTags();
        if (tags != null) {
            for (LocationTag tag : tags) {
                fhirLocation.addTag(new StringType(tag.getUuid()));

            }
        }
        return fhirLocation;
    }

}
