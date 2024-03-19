// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later
//
package com.opendatahub.timeseries.bdp.writer.writer.authz;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.opendatahub.timeseries.bdp.dto.dto.StationDto;

import jakarta.servlet.http.HttpServletRequest;

public class AuthorizeSyncStation {
    private static final Logger log = LoggerFactory.getLogger(AuthorizeSyncStation.class);

    public static void authorize(HttpServletRequest req, String stationType, List<StationDto> dtos, boolean syncState,
            boolean onlyActivation) {
        var authz = (Authorization) req.getAttribute(Authorization.ATTRIBUTE_AUTHORIZATION);
        if (authz == null) {
            // User is already authorized via role
            return;
        }
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

        var authorizedResources = authz.getAuthorizedResources("station", "write");
        log.debug("Got authorized resources from server: {}", authorizedResources);

        boolean authorized = authorizedResources.stream()
                .flatMap(r -> r.getUris().stream())
                .filter(u -> uriMatches(u, stationType, origin, syncState, onlyActivation))
                .findAny().isPresent();
                
        log.debug("Authorization on resource granted: {}", authorized);

        if (!authorized){
            throw new NotAuthorizedException("Missing authorization");
        }
    }

    private record Test(String name, BooleanSupplier condition){}

    public static boolean uriMatches(String uri, String stationType, String origin, boolean syncState, boolean onlyActivation) {
        var u = UriComponentsBuilder.fromUriString(uri).build();

        log.debug("Checking URI {}", u);
        return Arrays.stream(new Test[] {
            new Test("scheme", () -> "bdp".equals(u.getScheme())),
            new Test("authority", () -> "station".equals(u.getHost())),
            new Test("stationType", () -> getQueryParam(u, "stationType").anyMatch(s -> s.equals(stationType))),
            new Test("origin", () -> getQueryParam(u, "origin").anyMatch(s -> s.equals(origin))),
            new Test("syncState", () -> getQueryParam(u, "syncState").map(Boolean::parseBoolean).anyMatch(b -> b == syncState)),
            new Test("onlyActivation", () -> getQueryParam(u, "onlyActivation").map(Boolean::parseBoolean).anyMatch(b -> b == onlyActivation))
        })
        .allMatch(t -> {
            boolean result = t.condition.getAsBoolean();
            log.debug("Check {}: {}", t.name, result);
            return result;
        });
    }
    
    private static Stream<String> getQueryParam(UriComponents u, String param){
        return u.getQueryParams().getOrDefault(param, Collections.emptyList()).stream();
    }
}
