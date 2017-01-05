package org.openmrs.healthStandard.converter.fhir;

import org.openmrs.api.LocationService;

import ca.uhn.fhir.model.primitive.IdDt;
import org.hl7.fhir.dstu3.model.Address;
import org.hl7.fhir.dstu3.model.Location;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.StringType;
import org.openmrs.api.context.Context;
import org.openmrs.healthStandard.converter.Converter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Converter(type = org.openmrs.Location.class)
public class LocationConverter implements FHIRConverter<org.openmrs.Location, Location> {

    @Override
    public org.openmrs.Location toEMRResource(Location fhirLocation) {
        LocationService locationService = Context.getLocationService();
        org.openmrs.Location omrsLocation = locationService.getLocationByUuid(fhirLocation.getId());
        if (omrsLocation == null) {
            omrsLocation = new org.openmrs.Location();
            omrsLocation.setUuid(fhirLocation.getId());
        }
        omrsLocation.setName(fhirLocation.getName());
        omrsLocation.setDescription(fhirLocation.getDescription());

        //Set address
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
            String parentUuid = parentReference.getReference();
            org.openmrs.Location omrsLocationParent = locationService.getLocationByUuid(parentUuid);
            if (omrsLocationParent != null) {
                omrsLocation.setParentLocation(omrsLocationParent);
            }
        }
        return omrsLocation;
    }

    private void setAddressForOpenmrsLocation(org.openmrs.Location omrsLocation, List<StringType> addressStrings) {
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

    @Override
    public Location toFHIRResource(org.openmrs.Location omrsLocation) {
        Location fhirLocation = new Location();

        //Set resource id
        IdDt uuid = new IdDt();
        uuid.setValue(omrsLocation.getUuid());
        fhirLocation.setId(uuid);

        //Set name and location description
        fhirLocation.setName(omrsLocation.getName());
        fhirLocation.setDescription(omrsLocation.getDescription());

        //Set address
        Address address = new Address();
        address.setCity(omrsLocation.getCityVillage());
        address.setCountry(omrsLocation.getCountry());
        address.setState(omrsLocation.getStateProvince());
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

        if (!omrsLocation.isRetired()) {
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
        return fhirLocation;
    }
}
