package org.openmrs.module.fhir.mapper.emr;

import ca.uhn.fhir.model.dstu2.composite.ResourceReferenceDt;
import ca.uhn.fhir.model.dstu2.resource.Bundle;
import ca.uhn.fhir.model.dstu2.resource.MedicationOrder;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.DrugOrder;
import org.openmrs.Encounter;
import org.openmrs.Order;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.OrderService;
import org.openmrs.module.bahmniemrapi.drugorder.dosinginstructions.FlexibleDosingInstructions;
import org.openmrs.module.fhir.MRSProperties;
import org.openmrs.module.fhir.MapperTestHelper;
import org.openmrs.module.fhir.mapper.model.EmrEncounter;
import org.openmrs.module.fhir.mapper.model.ShrEncounterBundle;
import org.openmrs.module.fhir.utils.DateUtil;
import org.openmrs.module.fhir.utils.FHIRBundleHelper;
import org.openmrs.module.shrclient.dao.IdMappingRepository;
import org.openmrs.module.shrclient.model.IdMapping;
import org.openmrs.module.shrclient.model.IdMappingType;
import org.openmrs.module.shrclient.model.OrderIdMapping;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.annotation.DirtiesContext;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;
import static org.openmrs.module.fhir.MRSProperties.RESOURCE_MAPPING_EXTERNAL_ID_FORMAT;
import static org.openmrs.module.fhir.MapperTestHelper.getSystemProperties;

@org.springframework.test.context.ContextConfiguration(locations = {"classpath:TestingApplicationContext.xml"}, inheritLocations = true)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class FHIRMedicationOrderMapperIT extends BaseModuleWebContextSensitiveTest {

    private MapperTestHelper mapperTestHelper;
    @Autowired
    private ApplicationContext springContext;
    @Autowired
    private FHIRMedicationOrderMapper mapper;
    @Autowired
    private ConceptService conceptService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private IdMappingRepository idMappingRepository;
    @Autowired
    private EncounterService encounterService;

    private MedicationOrder medicationOrder;
    private Bundle medicationOrderBundle;

    @Before
    public void setUp() throws Exception {
        executeDataSet("testDataSets/drugOrderDS.xml");
        mapperTestHelper = new MapperTestHelper();

        medicationOrderBundle = (Bundle) mapperTestHelper.loadSampleFHIREncounter("encounterBundles/dstu2/medicationOrder.xml", springContext);
        medicationOrder = (MedicationOrder) FHIRBundleHelper.identifyFirstResourceWithName(medicationOrderBundle, new MedicationOrder().getResourceName());
    }

    @After
    public void tearDown() throws Exception {
        deleteAllData();
    }

    @Test
    public void shouldHandleResourceOfTypeMedicationPrescription() throws Exception {
        assertTrue(mapper.canHandle(medicationOrder));
    }

    @Test
    public void shouldMapMedicationToDrug() throws Exception {
        Order order = getOrder(medicationOrderBundle, medicationOrder);
        assertTrue(order instanceof DrugOrder);
        DrugOrder drugOrder = (DrugOrder) order;

        assertEquals(conceptService.getDrug(301), drugOrder.getDrug());
        assertEquals(conceptService.getConcept(301), drugOrder.getConcept());
        assertEquals(FlexibleDosingInstructions.class, drugOrder.getDosingType());
    }

    @Test
    public void shouldMapProviderAndSaveIdMapping() throws Exception {
        Order order = getOrder(medicationOrderBundle, medicationOrder);
        assertThat(order.getOrderer().getProviderId(), is(23));

        OrderIdMapping idMapping = (OrderIdMapping) idMappingRepository.findByInternalId(order.getUuid(), IdMappingType.MEDICATION_ORDER);
        assertNotNull(idMapping);
        assertEquals("shr-enc-id-1:7af48133-4c47-47d7-8d94-6a07abc18bf9", idMapping.getExternalId());
        assertEquals("http://shr.com/patients/98104750156/encounters/shr-enc-id-1#MedicationOrder/7af48133-4c47-47d7-8d94-6a07abc18bf9", idMapping.getUri());
    }

    @Test
    public void shouldSetCareSettingAndNumRefills() throws Exception {
        Order order = getOrder(medicationOrderBundle, medicationOrder);
        assertEquals("OutPatient", order.getCareSetting().getName());
        assertEquals(0, ((DrugOrder) order).getNumRefills().intValue());
    }

    @Test
    public void shouldMapDurationAndScheduledDateToDrug() throws Exception {
        Bundle bundle = (Bundle) mapperTestHelper.loadSampleFHIREncounter("encounterBundles/dstu2/encounterWithNonCodedMedicationOrderWithScheduledDate.xml", springContext);
        MedicationOrder resource = (MedicationOrder) FHIRBundleHelper.identifyFirstResourceWithName(bundle, new MedicationOrder().getResourceName());

        Order order = getOrder(bundle, resource);
        assertTrue(order instanceof DrugOrder);
        DrugOrder drugOrder = (DrugOrder) order;

        assertEquals(5, drugOrder.getDuration().intValue());
        assertEquals(conceptService.getConcept(803), drugOrder.getDurationUnits());

        assertEquals(DateUtil.parseDate("2015-09-24T00:00:00.000+05:30"), drugOrder.getScheduledDate());
        assertEquals(Order.Urgency.ON_SCHEDULED_DATE, drugOrder.getUrgency());
    }

    @Test
    public void shouldMapDrugRouteAndAsNeeded() throws Exception {
        Order order = getOrder(medicationOrderBundle, medicationOrder);
        assertTrue(order instanceof DrugOrder);
        DrugOrder drugOrder = (DrugOrder) order;

        assertEquals(conceptService.getConcept(701), drugOrder.getRoute());
        assertTrue(drugOrder.getAsNeeded());
    }

    @Test
    public void shouldMapDrugDosageAndFrequency() throws Exception {
        Order order = getOrder(medicationOrderBundle, medicationOrder);
        assertTrue(order instanceof DrugOrder);
        DrugOrder drugOrder = (DrugOrder) order;

        assertThat(drugOrder.getDose(), is(10.0));
        assertEquals(conceptService.getConcept(50), drugOrder.getDoseUnits());

        assertEquals(conceptService.getConcept(903), drugOrder.getFrequency().getConcept());
    }

    @Test
    public void shouldMapDoseFromMedicationFormsValueset() throws Exception {
        Bundle bundle = (Bundle) mapperTestHelper.loadSampleFHIREncounter("encounterBundles/dstu2/encounterWithNonCodedMedicationOrderWithScheduledDate.xml", springContext);
        MedicationOrder resource = (MedicationOrder) FHIRBundleHelper.identifyFirstResourceWithName(bundle, new MedicationOrder().getResourceName());

        Order order = getOrder(bundle, resource);
        assertTrue(order instanceof DrugOrder);
        DrugOrder drugOrder = (DrugOrder) order;

        assertThat(drugOrder.getDose(), is(10.0));
        assertEquals(conceptService.getConcept(807), drugOrder.getDoseUnits());
    }

    @Test
    public void shouldMapQuantityFromDispenseRequest() throws Exception {
        Order order = getOrder(medicationOrderBundle, medicationOrder);
        assertTrue(order instanceof DrugOrder);
        DrugOrder drugOrder = (DrugOrder) order;

        assertThat(drugOrder.getQuantity(), is(192.0));
        assertEquals(conceptService.getConcept(50), drugOrder.getQuantityUnits());
    }

    @Test
    public void shouldMapAdditionalInstructionsAndNotes() throws Exception {
        Order order = getOrder(medicationOrderBundle, medicationOrder);
        assertTrue(order instanceof DrugOrder);
        DrugOrder drugOrder = (DrugOrder) order;

        assertTrue(StringUtils.isNotBlank(drugOrder.getDosingInstructions()));
        assertEquals("additional instructions notes", readFromJson(drugOrder.getDosingInstructions(), MRSProperties.BAHMNI_DRUG_ORDER_ADDITIONAL_INSTRCTIONS_KEY));
        String instructionsConceptName = (String) readFromJson(drugOrder.getDosingInstructions(), MRSProperties.BAHMNI_DRUG_ORDER_INSTRCTIONS_KEY);
        assertEquals(conceptService.getConcept(1101).getName().getName(), instructionsConceptName);
    }

    @Test
    public void shouldMapDosageInstructionExtensionToDosingInstructions() throws Exception {
        Bundle bundle = (Bundle) mapperTestHelper.loadSampleFHIREncounter("encounterBundles/dstu2/medicationOrderWithCustomDosageInstruction.xml", springContext);
        MedicationOrder resource = (MedicationOrder) FHIRBundleHelper.identifyFirstResourceWithName(bundle, new MedicationOrder().getResourceName());

        Order order = getOrder(bundle, resource);
        assertTrue(order instanceof DrugOrder);
        DrugOrder drugOrder = (DrugOrder) order;

        assertNull(drugOrder.getDose());
        assertEquals(conceptService.getConcept(807), drugOrder.getDoseUnits());
        String dosingInstructions = drugOrder.getDosingInstructions();
        assertThat((Double) readFromJson(dosingInstructions, MRSProperties.BAHMNI_DRUG_ORDER_MORNING_DOSE_KEY), is(11.0));
        assertThat((Double) readFromJson(dosingInstructions, MRSProperties.BAHMNI_DRUG_ORDER_AFTERNOON_DOSE_KEY), is(12.0));
        assertThat((Double) readFromJson(dosingInstructions, MRSProperties.BAHMNI_DRUG_ORDER_EVENING_DOSE_KEY), is(13.0));
    }

    @Test
    public void shouldMapPriorPrescriptionEditedInDifferentEncounters() throws Exception {
        Bundle bundle = (Bundle) mapperTestHelper.loadSampleFHIREncounter("encounterBundles/dstu2/medicationOrderEditedInDifferentEncounter.xml", springContext);
        MedicationOrder resource = (MedicationOrder) FHIRBundleHelper.identifyFirstResourceWithName(bundle, new MedicationOrder().getResourceName());

        Order order = getOrder(bundle, resource);
        assertTrue(order instanceof DrugOrder);
        DrugOrder drugOrder = (DrugOrder) order;

        assertEquals(orderService.getOrder(15), drugOrder.getPreviousOrder());
    }

    @Test
    public void shouldMapPriorPrescriptionCreateAndMapOrdersEditedInSameEncounter() throws Exception {
        Bundle bundle = (Bundle) mapperTestHelper.loadSampleFHIREncounter("encounterBundles/dstu2/medicationOrderEditedInSameEncounter.xml", springContext);
        String editedOrderId = "vmkbja86-awaa-g1f3-9qv0-cccvc6c63ab0";
        String newOrderId = "zmkbja86-awaa-11f3-9qw4-ccc26cc6cabc";
        MedicationOrder resource = (MedicationOrder) FHIRBundleHelper.findResourceByReference(bundle, new ResourceReferenceDt("urn:uuid:" + newOrderId));

        Encounter mappedEncounter = encounterService.getEncounter(37);
        EmrEncounter emrEncounter = new EmrEncounter(mappedEncounter);
        ShrEncounterBundle encounterComposition = new ShrEncounterBundle(bundle, "98001080756", "shr-enc-id-1");
        mapper.map(resource, emrEncounter, encounterComposition, getSystemProperties("1"));
        assertEquals(2, emrEncounter.getOrders().size());

        IdMapping editedOrderMapping = idMappingRepository.findByExternalId(String.format(RESOURCE_MAPPING_EXTERNAL_ID_FORMAT, "shr-enc-id-1", editedOrderId), IdMappingType.MEDICATION_ORDER);
        Order editedOrder = findOrderByUuid(emrEncounter.getOrders(), editedOrderMapping.getInternalId());
        assertNotNull(editedOrder);

        IdMapping newOrderMapping = idMappingRepository.findByExternalId(String.format(RESOURCE_MAPPING_EXTERNAL_ID_FORMAT, "shr-enc-id-1", newOrderId), IdMappingType.MEDICATION_ORDER);
        Order newOrder = findOrderByUuid(emrEncounter.getOrders(), newOrderMapping.getInternalId());
        assertNotNull(newOrder);

        assertEquals(editedOrder, newOrder.getPreviousOrder());
    }

    @Test
    public void shouldMapOrderActionForNewOrder() throws Exception {
        DrugOrder drugOrder = (DrugOrder) getOrder(medicationOrderBundle, medicationOrder);
        assertEquals(Order.Action.NEW, drugOrder.getAction());
    }

    @Test
    public void shouldMapOrderActionForStoppedOrder() throws Exception {
        Bundle bundle = (Bundle) mapperTestHelper.loadSampleFHIREncounter("encounterBundles/dstu2/medicationOrderWithCustomDosageInstruction.xml", springContext);
        MedicationOrder resource = (MedicationOrder) FHIRBundleHelper.identifyFirstResourceWithName(bundle, new MedicationOrder().getResourceName());
        DrugOrder drugOrder = (DrugOrder) getOrder(bundle, resource);
        assertEquals(Order.Action.DISCONTINUE, drugOrder.getAction());
    }

    @Test
    public void shouldMapOrderActionForRevisedOrder() throws Exception {
        Bundle bundle = (Bundle) mapperTestHelper.loadSampleFHIREncounter("encounterBundles/dstu2/medicationOrderEditedInDifferentEncounter.xml", springContext);
        MedicationOrder resource = (MedicationOrder) FHIRBundleHelper.identifyFirstResourceWithName(bundle, new MedicationOrder().getResourceName());
        DrugOrder drugOrder = (DrugOrder) getOrder(bundle, resource);
        assertEquals(Order.Action.REVISE, drugOrder.getAction());
    }

    @Test
    public void shouldMapMedicationOrderWithoutPrescriber() throws Exception {
        String bundleWithoutPrescriber = "encounterBundles/dstu2/encounterWithNonCodedMedicationOrderWithScheduledDate.xml";
        Bundle bundle = (Bundle) mapperTestHelper.loadSampleFHIREncounter(bundleWithoutPrescriber, springContext);
        MedicationOrder resource = (MedicationOrder) FHIRBundleHelper.identifyFirstResourceWithName(bundle, new MedicationOrder().getResourceName());
        DrugOrder drugOrder = (DrugOrder) getOrder(bundle, resource);
        assertThat(drugOrder.getOrderer().getProviderId(), is(22));
    }

    @Test
    public void shouldMapNonCodedDrugs() throws Exception {
        String bundleWithNonCodedDrug = "encounterBundles/dstu2/encounterWithNonCodedMedicationOrderWithScheduledDate.xml";
        Bundle bundle = (Bundle) mapperTestHelper.loadSampleFHIREncounter(bundleWithNonCodedDrug, springContext);
        MedicationOrder resource = (MedicationOrder) FHIRBundleHelper.identifyFirstResourceWithName(bundle, new MedicationOrder().getResourceName());
        DrugOrder drugOrder = (DrugOrder) getOrder(bundle, resource);
        assertNull(drugOrder.getDrug());
        assertEquals("Paracetamol 20mg", drugOrder.getDrugNonCoded());
        assertEquals(conceptService.getConcept(103), drugOrder.getConcept());
    }

    @Test
    public void shouldMapMedicationOrderWithoutDoseRouteAndAdditionalInstructions() throws Exception {
        String bundleWithNonCodedDrug = "encounterBundles/dstu2/medicationOrderWithoutDoseRouteAndAdditionalInstructions.xml";

        Bundle bundle = (Bundle) mapperTestHelper.loadSampleFHIREncounter(bundleWithNonCodedDrug, springContext);

        MedicationOrder resource = (MedicationOrder) FHIRBundleHelper.identifyFirstResourceWithName(bundle, new MedicationOrder().getResourceName());
        DrugOrder drugOrder = (DrugOrder) getOrder(bundle, resource);

        assertNull(drugOrder.getDose());
        assertNull(drugOrder.getDoseUnits());
        String dosingInstructions = drugOrder.getDosingInstructions();
        assertNull(readFromJson(dosingInstructions, MRSProperties.BAHMNI_DRUG_ORDER_MORNING_DOSE_KEY));
        assertNull(readFromJson(dosingInstructions, MRSProperties.BAHMNI_DRUG_ORDER_AFTERNOON_DOSE_KEY));
        assertNull(readFromJson(dosingInstructions, MRSProperties.BAHMNI_DRUG_ORDER_EVENING_DOSE_KEY));

        assertNull(drugOrder.getRoute());

        assertNull(readFromJson(drugOrder.getDosingInstructions(), MRSProperties.BAHMNI_DRUG_ORDER_INSTRCTIONS_KEY));
    }

    @Test
    public void shouldNotCreateOrderIfAlreadyPresentInEncounter() throws Exception {
        EmrEncounter emrEncounter = new EmrEncounter(encounterService.getEncounter(39));

        ShrEncounterBundle encounterComposition = new ShrEncounterBundle(medicationOrderBundle, "98104750156", "shr-enc-id-4");
        mapper.map(medicationOrder, emrEncounter, encounterComposition, getSystemProperties("1"));

        assertEquals(0, emrEncounter.getOrders().size());
    }

    @Test
    public void shouldNotCreatePreviousOrderInSameSessionIfAlreadyDownloaded() throws Exception {
        Bundle bundle = (Bundle) mapperTestHelper.loadSampleFHIREncounter("encounterBundles/dstu2/medicationOrderEditedInSameEncounter.xml", springContext);
        String shrEncounterId = "shr-enc-id-5";
        String editedOrderId = "vmkbja86-awaa-g1f3-9qv0-cccvc6c63ab0";
        String newOrderId = "zmkbja86-awaa-11f3-9qw4-ccc26cc6cabc";
        MedicationOrder resource = (MedicationOrder) FHIRBundleHelper.findResourceByReference(bundle, new ResourceReferenceDt("urn:uuid:" + newOrderId));

        Encounter mappedEncounter = encounterService.getEncounter(40);
        assertEquals(1, mappedEncounter.getOrders().size());
        IdMapping editedOrderIdMapping = idMappingRepository.findByExternalId(String.format(RESOURCE_MAPPING_EXTERNAL_ID_FORMAT, shrEncounterId, editedOrderId),
                IdMappingType.MEDICATION_ORDER);
        assertEquals(mappedEncounter.getOrders().iterator().next().getUuid(), editedOrderIdMapping.getInternalId());

        EmrEncounter emrEncounter = new EmrEncounter(mappedEncounter);
        ShrEncounterBundle encounterComposition = new ShrEncounterBundle(bundle, "98001080756", shrEncounterId);
        mapper.map(resource, emrEncounter, encounterComposition, getSystemProperties("1"));
        assertEquals(1, emrEncounter.getOrders().size());

        IdMapping newOrderIdMapping = idMappingRepository.findByExternalId(String.format(RESOURCE_MAPPING_EXTERNAL_ID_FORMAT, shrEncounterId, newOrderId),
                IdMappingType.MEDICATION_ORDER);
        assertEquals(emrEncounter.getOrders().iterator().next().getUuid(), newOrderIdMapping.getInternalId());
    }

    private Object readFromJson(String json, String key) throws IOException {
        Map map = new ObjectMapper().readValue(json, Map.class);
        return map.get(key);
    }

    private Order getOrder(Bundle bundle, MedicationOrder resource) {
        Encounter mappedEncounter = encounterService.getEncounter(37);
        EmrEncounter emrEncounter = new EmrEncounter(mappedEncounter);

        ShrEncounterBundle encounterComposition = new ShrEncounterBundle(bundle, "98104750156", "shr-enc-id-1");
        mapper.map(resource, emrEncounter, encounterComposition, getSystemProperties("1"));

        assertEquals(1, emrEncounter.getOrders().size());
        return emrEncounter.getOrders().iterator().next();
    }

    private Order findOrderByUuid(Set<Order> orders, String uuid) {
        for (Order order : orders) {
            if (order.getUuid().equals(uuid))
                return order;
        }
        return null;
    }
}