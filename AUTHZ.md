# Writer API Authorization
Users accessing the API are authorized via Keycloak / Oauth

## Role based
The legacy way of authorizing access to the API is via roles.  
Users that wish to access the API need to have an `ADMIN` role on the writer client (in production `odh-mobility-writer`), and have that role mapped.  
Users that have the admin role can freely access all endpoints

## UMA (User managed Authorization)
For users that don't have the ADMIN role, the API tries to lookup fine grained resource level authorizations, handled by Keycloak authorization services.
Currently this is only implemented for syncStations, all other endpoints are only accessible as ADMIN role.  
Resources are defined as URIs in a custom format:

`bdp://station?origin=neogy&stationType=EChargingStation&syncState=false&onlyActivation=false`

Which enables access to sync stations with `origin=neogy`, `stationType=EChargingStation`, and only when the flags `syncState` and `onlyActivation` are both set to `false`  

Multiple URIs can be defined per resource, only one has to match to be granted access.


