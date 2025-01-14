// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later
package com.opendatahub.timeseries.bdp.writer;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.opendatahub.timeseries.bdp.writer.writer.authz.AuthorizeSyncStation;

public class AuthorizationTest {
    @Test
    public void testStationSyncUrlMatch() {
        // actually matches what's in the URL
        var uri = "bdp://station?origin=testorigin&stationType=testtype&syncState=false&onlyActivation=true";
        assertTrue(AuthorizeSyncStation.uriMatches(uri, "testtype", "testorigin", false, true));
        assertFalse(AuthorizeSyncStation.uriMatches(uri, "EchargingStation", "testorigin", false, true));
        assertFalse(AuthorizeSyncStation.uriMatches(uri, "testtype", "A22", false, true));
        assertFalse(AuthorizeSyncStation.uriMatches(uri, "testtype", "testorigin", true, true));
        assertFalse(AuthorizeSyncStation.uriMatches(uri, "testtype", "testorigin", false, false));

        uri = "bdp://station?origin=testorigin&syncState=false&onlyActivation=true";
        // Missing stationType in URL
        assertFalse(AuthorizeSyncStation.uriMatches(uri, "EchargingStation", "testorigin", false, true));

        uri = "bdp://station?origin=testorigin&stationType=test1&stationType=test2&syncState=false&onlyActivation=true";
        // duplicate parameter
        assertTrue(AuthorizeSyncStation.uriMatches(uri, "test1", "testorigin", false, true));
        assertTrue(AuthorizeSyncStation.uriMatches(uri, "test2", "testorigin", false, true));
    }
}
