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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class FhirToOpenMRSLocationConverter implements FHIRConverter<FhirLocation, org.openmrs.Location> {

    public static final int MAX_ADDRESS_LINES = 15;
    private LocationService locationService;

    @Override
    public org.openmrs.Location convert(FhirLocation fhirLocation) {
        locationService = Context.getLocationService();
        org.openmrs.Location omrsLocation = getOpenMrsLocation(fhirLocation);
        omrsLocation.setName(fhirLocation.getName());
        omrsLocation.setDescription(fhirLocation.getDescription());
        updateAddress(fhirLocation, omrsLocation);
        updatePosition(fhirLocation, omrsLocation);
        updateStatus(fhirLocation, omrsLocation);
        updateParentLocation(fhirLocation, omrsLocation);
        updateLocationTags(fhirLocation, omrsLocation);
        return omrsLocation;
    }

    private void updateLocationTags(FhirLocation fhirLocation, org.openmrs.Location omrsLocation) {
        Set<LocationTag> locationTags = fhirLocation.getFhirLocationTags()
                .stream()
                .map(this::findLocationTag)
                .collect(Collectors.toSet());

        omrsLocation.setTags(locationTags);
    }

    private LocationTag findLocationTag(StringType fhirLocationTag) {
        String locationTagUuid = fhirLocationTag.getValue();
        LocationTag locationTag = locationService.getLocationTagByUuid(locationTagUuid);
        if(locationTag == null){
            throw new EntityNotFoundException(String.format("Location Tag with uuid=%s does not exist", locationTagUuid));
        }
        return locationTag;
    }

    private void updateParentLocation(FhirLocation fhirLocation, org.openmrs.Location omrsLocation) {
        Reference parentReference = fhirLocation.getPartOf();
        String parentUuid = parentReference.getReferenceElement().getIdPart();
        org.openmrs.Location omrsLocationParent = null;
        if (parentUuid != null) {
            omrsLocationParent = locationService.getLocationByUuid(parentUuid);
            if (omrsLocationParent == null) {
                throw new EntityNotFoundException(String.format("Parent Location with uuid=%s does not exist", parentUuid));
            }
        }
        omrsLocation.setParentLocation(omrsLocationParent);
    }

    private void updateStatus(FhirLocation fhirLocation, org.openmrs.Location omrsLocation) {
        String status = fhirLocation.getStatus().toString();
        if (status.equalsIgnoreCase(Location.LocationStatus.ACTIVE.toString())) {
            omrsLocation.setRetired(false);
        } else if (status.equalsIgnoreCase((Location.LocationStatus.INACTIVE.toString()))) {
            omrsLocation.setRetired(true);
        }
    }

    private void updatePosition(FhirLocation fhirLocation, org.openmrs.Location omrsLocation) {
        Location.LocationPositionComponent position = fhirLocation.getPosition();
        BigDecimal latitute = position.getLatitude();
        BigDecimal longitute = position.getLongitude();
        if (latitute != null && longitute != null) {
            omrsLocation.setLatitude(latitute.toString());
            omrsLocation.setLongitude(longitute.toString());
        }
    }

    private void updateAddress(FhirLocation fhirLocation, org.openmrs.Location omrsLocation) {
        Address address = fhirLocation.getAddress();
        omrsLocation.setCityVillage(address.getCity());
        omrsLocation.setCountry(address.getCountry());
        omrsLocation.setStateProvince(address.getState());
        omrsLocation.setCountyDistrict(address.getDistrict());
        omrsLocation.setPostalCode(address.getPostalCode());
        List<StringType> addressStrings = address.getLine();
        setAddressLinesForOpenmrsLocation(omrsLocation, addressStrings);
    }

    private org.openmrs.Location getOpenMrsLocation(FhirLocation fhirLocation) {
        org.openmrs.Location omrsLocation = locationService.getLocationByUuid(fhirLocation.getIdElement().getIdPart());
        if (omrsLocation == null) {
            omrsLocation = new org.openmrs.Location();
            omrsLocation.setUuid(fhirLocation.getIdElement().getIdPart());
        }
        return omrsLocation;
    }

    private void setAddressLinesForOpenmrsLocation(org.openmrs.Location omrsLocation, List<StringType> addressStrings) {
        for (int i = 0; i < addressStrings.size() && i < MAX_ADDRESS_LINES; i++) {
            String methodName = "setAddress" + (i + 1);
            try {
                Method method = org.openmrs.Location.class.getMethod(methodName, String.class);
                method.invoke(omrsLocation, addressStrings.get(i).getValue());
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                throw new RuntimeException("Error executing method " + methodName, e);
            }
        }
    }
}
