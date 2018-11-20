/**
 * BDP - Tests for the Big Data Platform
 * Copyright © 2016-2018 IDM Südtirol - Alto Adige (info@idm-suedtirol.com)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program (see LICENSES/GPL-3.0.txt). If not, see
 * <http://www.gnu.org/licenses/>.
 *
 * SPDX-License-Identifier: GPL-3.0
 */

var chakram = require('chakram');
expect = chakram.expect;
var webserviceHost = 'http://127.0.0.1:8081/';
var webserviceEndPoints = ['writer/json/'];

var checkErrorResponse = function(response) {
	expect(response).to.have.header("content-type", "application/json;charset=UTF-8");
	expect(response).to.have.schema({"status":"int", "name":"string", "description":"string"});
}

var checkBadRequest = function(response) {
	checkErrorResponse(response);
	expect(response).to.have.status(400);
	expect(response.body.status).to.equal(400)
	expect(response.body.name).to.contain("Bad Request");
}

var checkNotFound = function(response) {
	checkErrorResponse(response);
	expect(response).to.have.status(404);
	expect(response.body.status).to.equal(404)
	expect(response.body.name).to.contain("Not Found");
}

describe("BDP WRITER v1", function() {
	webserviceEndPoints.forEach(function(webservice) {
		var method = "getDateOfLastRecord"
		var stationType = "ST"
		var url = webserviceHost + webservice + method;
		it("Missing path variables: " + url, function () {
			return chakram.get(url).then(function (response) {
				checkBadRequest(response);
				expect(response.body.description).to.contain("Missing station type");
				return chakram.wait();
			});
		});
		var currentUrl = url + "/" + stationType;
		it("Single parameter stationId: " + currentUrl, function () {
			return chakram.get(currentUrl + "?stationId=").then(function (response) {
				checkBadRequest(response);
				expect(response.body.description).to.contain("Required String parameter 'typeId'");
				return chakram.get(currentUrl + "?stationId=X")
			}).then(function (response) {
				checkBadRequest(response);
				expect(response.body.description).to.contain("Required String parameter 'typeId'");
				return chakram.wait();
			});
		});
		it("All parameters present: " + currentUrl, function () {
			return chakram.get(currentUrl + "?stationId=&typeId=").then(function (response) {
				checkBadRequest(response);
				expect(response.body.description).to.contain("Invalid parameter value");
				return chakram.get(currentUrl + "?stationId=X&typeId=")
			}).then(function (response) {
				checkBadRequest(response);
				expect(response.body.description).to.contain("Invalid parameter value");
				return chakram.get(currentUrl + "?stationId=&typeId=X");
			}).then(function (response) {
				checkBadRequest(response);
				expect(response.body.description).to.contain("Invalid parameter value");
				return chakram.get(currentUrl + "?stationId=NOTEXISTING&typeId=Y");
			}).then(function (response) {
				checkNotFound(response);
				expect(response.body.description).to.match(/^Station '[A-Za-z0-9_-]*' not found/);
				return chakram.get(currentUrl + "?stationId=X&typeId=Y");
			}).then(function (response) {
				checkNotFound(response);
				expect(response.body.description).to.match(/^Station '[A-Za-z0-9_-]*' not found/);
				return chakram.wait();
			});
		});
	});
});
