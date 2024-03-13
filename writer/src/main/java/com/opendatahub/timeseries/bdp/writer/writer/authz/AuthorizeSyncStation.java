// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later
//
package com.opendatahub.timeseries.bdp.writer.writer.authz;

import java.util.Arrays;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.UriComponentsBuilder;

import com.opendatahub.timeseries.bdp.dto.dto.StationDto;

import jakarta.servlet.http.HttpServletRequest;

public class AuthorizeSyncStation {
    private static final Logger log = LoggerFactory.getLogger(AuthorizeSyncStation.class);

    public static void authorize(HttpServletRequest req, String stationType, List<StationDto> dtos, boolean syncState,
            boolean onlyActivation) {
        log.debug("Start authorizing station sync");

        var origins = dtos.stream()
                .map(StationDto::getOrigin)
                .collect(Collectors.toSet());

        // Technically we could handle multiple origins, but the state synchronization
        // doesn't behave correctly anyway
        if (origins.size() != 1) {
            throw new NotAuthorizedException("mixed or missing origins");
        }
        var origin = origins.stream().findFirst().get();
        
        log.debug("Start evaluating UMA for stationType = {}, origin = {}, syncState = {}, onlyActivation = {}", stationType, origin, syncState, onlyActivation);

        var authz = (Authorization) req.getAttribute(Authorization.ATTRIBUTE_AUTHORIZATION);

        var authorizedResources = authz.getAuthorizedResources("station", "write");
        
        log.debug("Got authorized resources from server: {}", authorizedResources);

        boolean authorized = authorizedResources.stream()
                .flatMap(r -> r.getUris().stream())
                .map(s -> UriComponentsBuilder.fromUriString(s).build())
                // it's just a bunch of lambdas that produce booleans, and all have to be true
                .filter(u -> Arrays.stream(new BooleanSupplier[] {
                        () -> "bdp".equals(u.getScheme()),
                        () -> "station".equals(u.getSchemeSpecificPart()),
                        () -> u.getQueryParams().get("stationType").stream().anyMatch(s -> s.equals(stationType)),
                        () -> u.getQueryParams().get("origin").stream().anyMatch(s -> s.equals(origin)),
                        () -> u.getQueryParams().get("syncState").stream().map(Boolean::getBoolean)
                                .anyMatch(b -> b == syncState),
                        () -> u.getQueryParams().get("onlyActivation").stream().map(Boolean::getBoolean)
                                .anyMatch(b -> b == onlyActivation)
                }).allMatch(BooleanSupplier::getAsBoolean))
                .findAny().isPresent();
                
        log.debug("Authorization on resource granted: {}", authorized);

        if (!authorized){
            throw new NotAuthorizedException("Missing authorization");
        }
    }
}
