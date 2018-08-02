var chakram = require('chakram');
expect = chakram.expect;
var webserviceHost = 'http://ipchannels.integreen-life.bz.it/';
var webserviceEndPoints = ['bikesharing','bluetooth','carpooling','carsharing','roadweather','emobility','environment','meteorology','origin-destination','parkingFrontEnd','street','sensors'];
var abstractCalls = ['get-stations','get-station-details','get-data-types'];
describe("BDP WS v1:", function() {
  webserviceEndPoints.forEach(function(webservice){
    var url = webserviceHost + webservice + "/rest/";
    abstractCalls.forEach(function(call){
      var abstractCall = url + call;
      it("Service availability check : \""+abstractCall+"\"", function () {
        var response = chakram.get(abstractCall);
        expect(response).to.have.status(200);
        expect(response).to.have.header("content-type", "application/json;charset=UTF-8");
        expect(response).to.have.header("Access-Control-Allow-Origin","*");
        return chakram.wait();
      });
    });
    it("Datatype request with parameter - " + webservice, function () {
      var r = chakram.get(url+"get-stations");
      expect(r).to.have.schema({"type":"array"});
      return chakram.wait()
      .then(function(sResponse){
        var firstStation = sResponse.body[0];
        var reqUrl = url+"get-data-types?station=" + firstStation;
        var response = chakram.get(reqUrl);
        expect(response).to.have.header("content-type", "application/json;charset=UTF-8");
        expect(response).to.have.status(200);
        expect(response).to.have.schema({"type":"array"});
        return chakram.wait();
      });
    });
    it("Newest data request with parameter - " + webservice, function () {
      var firstStation;
      return chakram.get(url+"get-stations")
      .then(function(r){
        firstStation = r.body[0];
        return chakram.get(url+"get-data-types?station=" + firstStation);
      })
      .then(function(promise){
        if (promise.body.length>0){
          var reqUrl = url+"get-newest-record?station=" + firstStation + "&type=" + promise.body[0][0]+"&period=" + promise.body[0][3];
          var response = chakram.get(reqUrl);
          expect(response).to.have.status(200);
          expect(response).to.have.header("content-type","application/json;charset=UTF-8");
          expect(response).to.have.schema({"type":"object"});
        }
        return chakram.wait()
      }).then(function (dataResponse){
	var latestRecord = dataResponse.body;
	expect(latestRecord).not.to.be.null;
	expect(latestRecord).to.have.property('value');
	expect(latestRecord).to.have.property('timestamp');
	return chakram.wait();
	});
    });
    it("Get random data of last 2 hours - " + webservice, function () {
      var firstStation;
      return chakram.get(url+"get-stations")
      .then(function(r){
        firstStation = r.body[0];
        return chakram.get(url+"get-data-types?station=" + firstStation);
      })
      .then(function(promise){
        if (promise.body.length>0){
          var reqUrl = url+"get-records?station=" + firstStation + "&name=" + promise.body[0][0] + "&period=" + promise.body[0][3] + "&seconds=7200";
          //console.log("\t\""+reqUrl+"\"");
          var response = chakram.get(reqUrl);
          expect(response).to.have.status(200);
          expect(response).to.have.header("content-type","application/json;charset=UTF-8");
          expect(response).to.have.schema({"type":"array"});
        }
        return chakram.wait()
      });
    });
    it("Get random data of 2 hours on 30 November 2016 - "  + webservice, function () {
      var firstStation;
      return chakram.get(url+"get-stations")
      .then(function(r){
        firstStation = r.body[0];
        return chakram.get(url+"get-data-types?station=" + firstStation);
      })
      .then(function(promise){
        if (promise.body.length>0){
          var reqUrl = url+"get-records-in-timeframe?station=" + firstStation + "&name=" + promise.body[0][0] + "&period=" + promise.body[0][3] + "&from=1480496400000&to=1480503600000";
          //console.log("\t\""+reqUrl+"\"");
          var response = chakram.get(reqUrl);
          expect(response).to.have.status(200);
          expect(response).to.have.header("content-type","application/json;charset=UTF-8");
          expect(response).to.have.schema({"type":"array"});
        }
        return chakram.wait()
      });
    });
  });
});
/*var abstractCalls = ['station-ids','station-details','types'];
describe("BDP WS v2:", function() {
  webserviceEndPoints.forEach(function(webservice){
    var url = webserviceHost + webservice + "/rest/v2/";
    abstractCalls.forEach(function(call){
      var abstractCall = url + call;
      it("Service availability check : \""+abstractCall+"\"", function () {
        var response = chakram.get(abstractCall);
        expect(response).to.have.status(200);
        expect(response).to.have.header("content-type", "application/json;charset=UTF-8");
        expect(response).to.have.header("Access-Control-Allow-Origin","*");
        return chakram.wait();
      });
    });
    it("Newest data request with parameter - " + webservice, function () {
      var firstStation;
      return chakram.get(url+"station-ids")
      .then(function(r){
        firstStation = r.body[0];
        return chakram.get(url+"types?station=" + firstStation);
      })
      .then(function(promise){
        if (promise.body.length>0){
          var reqUrl = url+"newest?station=" + firstStation + "&type=" + promise.body[0].id;
          if (promise.body[0].aquisitionIntervalls[0].length>0)
          reqUrl+= +"&period=" + promise.body[0].aquisitionIntervalls[0];
          var response = chakram.get(reqUrl);
          expect(response).to.have.status(200);
          expect(response).to.have.header("content-type","application/json;charset=UTF-8");
          expect(response).to.have.schema({"type":"object"});
        }
        return chakram.wait()
      });
    });
    it("Get random data of last 2 hours - " + webservice, function () {
      var firstStation;
      return chakram.get(url+"station-ids")
      .then(function(r){
        firstStation = r.body[0];
        return chakram.get(url+"types?station=" + firstStation);
      })
      .then(function(promise){
        if (promise.body.length>0){
          var reqUrl = url+"history?station=" + firstStation + "&type=" + promise.body[0].id + "&seconds=7200";
          if (promise.body[0].aquisitionIntervalls.length>0)
          reqUrl+= +"&period=" + promise.body[0].aquisitionIntervalls[0];
          var response = chakram.get(reqUrl);
          expect(response).to.have.status(200);
          expect(response).to.have.header("content-type","application/json;charset=UTF-8");
          expect(response).to.have.schema({"type":"array"});
        }
        return chakram.wait()
      });
    });

  });
});*/
