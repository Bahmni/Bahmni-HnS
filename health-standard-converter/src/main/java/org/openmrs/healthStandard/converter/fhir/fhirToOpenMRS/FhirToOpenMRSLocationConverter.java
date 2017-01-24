package org.openmrs.healthStandard.converter.fhir.fhirToOpenMRS;

import org.hl7.fhir.dstu3.model.Address;
import org.hl7.fhir.dstu3.model.Location;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.StringType;
import org.openmrs.LocationTag;
import org.openmrs.api.LocationService;
import org.openmrs.api.context.Context;
import org.openmrs.healthStandard.converter.exceptions.EntityNotFoundException;
import org.openmrs.healthStandard.converter.fhir.FHIRConverter;
import org.openmrs.healthStandard.converter.fhir.fhirModels.FhirLocation;

import java.math.BigDecimal;
import java.util.List;

public class FhirToOpenMRSLocationConverter implements FHIRConverter<FhirLocation, org.openmrs.Location> {
    @Override
    public org.openmrs.Location convert(FhirLocation fhirLocation) {
        LocationService locationService = Context.getLocationService();
        org.openmrs.Location omrsLocation = locationService.getLocationByUuid(fhirLocation.getIdElement().getIdPart());
        if (omrsLocation == null) {
            omrsLocation = new org.openmrs.Location();
            omrsLocation.setUuid(fhirLocation.getIdElement().getIdPart());
        }
        omrsLocation.setName(fhirLocation.getName());
        omrsLocation.setDescription(fhirLocation.getDescription());

        Address address = fhirLocation.getAddress();
        omrsLocation.setCityVillage(address.getCity());
        omrsLocation.setCountry(address.getCountry());
        omrsLocation.setStateProvince(address.getState());
        omrsLocation.setCountyDistrict(address.getDistrict());
        omrsLocation.setPostalCode(address.getPostalCode());
        List<StringType> addressStrings = address.getLine();
        setAddressForOpenmrsLocation(omrsLocation, addressStrings);

        Location.LocationPositionComponent position = fhirLocation.getPosition();
        BigDecimal latitute = position.getLatitude();
        BigDecimal longitute = position.getLongitude();
        if (latitute != null && longitute != null) {
            omrsLocation.setLatitude(latitute.toString());
            omrsLocation.setLongitude(longitute.toString());
        }
        String status = fhirLocation.getStatus().toString();
        if (status.equalsIgnoreCase(Location.LocationStatus.ACTIVE.toString())) {
            omrsLocation.setRetired(false);
        } else if (status.equalsIgnoreCase((Location.LocationStatus.INACTIVE.toString()))) {
            omrsLocation.setRetired(true);
        }

        Reference parentReference = fhirLocation.getPartOf();
        if (parentReference != null) {
            String parentUuid = parentReference.getReferenceElement().getIdPart();
            org.openmrs.Location omrsLocationParent = locationService.getLocationByUuid(parentUuid);
            if (omrsLocationParent != null) {
                omrsLocation.setParentLocation(omrsLocationParent);
            }
        }
        for (StringType fhirLocationTag : (fhirLocation).getFhirLocationTags()) {
            String uuid = fhirLocationTag.getValue();
            LocationTag locationTag = locationService.getLocationTagByUuid(uuid);
            if (locationTag == null) {
                throw new EntityNotFoundException(String.format("Location Tag with uuid=%s does not exist", uuid));
            }
            omrsLocation.addTag(locationTag);
        }
        return omrsLocation;
    }


    private void setAddressForOpenmrsLocation(org.openmrs.Location omrsLocation, List<StringType> addressStrings) {
        //TODO:clean up
        for (int i = 0; i < addressStrings.size(); i++) {
            switch (i + 1) {
                case 1:
                    omrsLocation.setAddress1(addressStrings.get(i).toString());
                    break;
                case 2:
                    omrsLocation.setAddress2(addressStrings.get(i).toString());
                    break;
                case 3:
                    omrsLocation.setAddress3(addressStrings.get(i).toString());
                    break;
                case 4:
                    omrsLocation.setAddress4(addressStrings.get(i).toString());
                    break;
                case 5:
                    omrsLocation.setAddress5(addressStrings.get(i).toString());
                    break;
            }
        }
    }
}
