/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at https://www.bahmni.org/license/mplv2hd.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.fhir.utils;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import static org.junit.Assert.assertEquals;

@org.springframework.test.context.ContextConfiguration(locations = {"classpath:TestingApplicationContext.xml"}, inheritLocations = true)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class GlobalPropertyLookupServiceIT extends BaseModuleWebContextSensitiveTest {

    @Autowired
    GlobalPropertyLookUpService globalPropertyLookUpService;

    @Before
    public void setUp() throws Exception {
        executeDataSet("testDataSets/omrsGlobalPropertyTestDS.xml");
    }

    @After
    public void tearDown() throws Exception {
        deleteAllData();
    }


    @Test
    public void shouldReturnConfiguredGlobalPropertyValue() throws Exception {
        assertEquals(555, Integer.parseInt(globalPropertyLookUpService.getGlobalPropertyValue("concept.causeOfDeath")));
    }
}