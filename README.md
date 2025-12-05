# cataloger-of-walking-routes

## Introduction
This is my first java/SpringBoot training project. The creation of this project does not set any financial goals and does not offer innovative solutions to existing technical problems. My primary goal when working on this project is to gain practical experience in java and learn more about this programming language and related libraries and frameworks.

Nevertheless, here is the scope of desired functionality, which I try to adhere to. During my work, I maintain a backlog and documentation, trying to fix the developed functionality and future plans.

On "release" I would like to see a service that could potentially be deployed on a server and hosting for using it over the Internet.

I realize that, at least in its current form, the project can hardly be of practical interest to anyone but me, but nevertheless, I would be grateful for any constructive comments and suggestions.

## Description
Service working with users walking routes. The main functionality of the service is assumed to be as follows:
1. Saving of the user route plan (a sequence of points on the map) built in one of the popular public mapping services (google.Maps, yandex.Maps, 2gis, etc.) by transferring its hyperlink
2. Automatic calculation and saving of the main parameters of the transmitted route, which are important for the user's choice. The calculation uses data from various open-source mapping services:
- the total length of the route - according to the data from Open Source Routing Machine (OSRM) service (implementations jawg.io )
- iformation about public transport stops and their type near the route endpoints - according to the data from OpenStreetMaps (OSM) service
- the distance between route endpoints and nearest public transport stations, distance between route endpoints (to understand whether this route can be considered a ring) - according to OSRM data
- total height of ascent and descent on the route - according to OSRM data
- regions through which the route passes (only an idea so far)
- etc.
3. Providing to the user a list of suitable routes, satisfying the filtering parameters he set
4. Constructing and providing to the user hiperlink on mapping service (google.Maps, yandex.Maps, 2gis, etc.) for the requested route  
5. Functioning of the service as a social network for walkers - the possibility of user registration and authorization, route exchenge, ratings and comments (plans for the distant future)
In its initial form, the service provides a rest api for interacting with it, in the future I would like to make a simple web interface.

## Done now

1. Saving of route from url (only yandex) via rest api
2. Scheduled calculation of route length by data from OSRM
3. Scheduled getting nearest to route endpoints public transport stops
4. Rest api for routes
5. Integrational tests for routes (only positive scenarios)

## Future plans

1. Scheduled calculation of total ascent and descent height on the route - according to OSRM data
2. Scheduled getting of distances from route endpoints and public transport stops
3. Maintaining for different mapping services urls (google, 2gis, etc.)
4. Refactoring rest api to full functionality
5. More integration and unit tests for all the functionality
6. Global code refactoring
7. Multiple users functionality:
   - Authorization and roles
   - User profiles
   - Commenting, ratings, compilations of routes
8. Web-interfaces
9. etc.  

## Specification (actual)
### DB scheme
![DB scheme](https://github.com/eevgeniy86/cataloger-of-walking-routes/blob/main/png/cataloger-of-walking-routes%20DB.png)

[Source](https://app.diagrams.net/#G1bQZYkkVOpQfT3XxWXYR6dxSE6pfxUY9s#%7B"pageId"%3A"z3W7p9jmMmL0QTRQeyUE"%7D)

**route** - main table for saved routes with basic information

**waypoint** - sequence of points on the map defined by user in his route plan

**station** - information about public transport stops near route endpoints, got from OSM

**point** - geographical coordinates of waypoints and stations

**relation** - describes useful relations for route waypoints - distances between route endpoints and nearest public transport stations, distances between route endpoints

### Rest api

**routes**

- POST */route/url* - save route from mapping service url (at the moment yandex.Maps only)
- POST */route* - save route from json-object
- GET */route/{id}* - get route by id as json-object
- GET */route/{id}/url* - get route url for mapping service (at the moment yandex.Maps only)
- GET */route* - get routes with filtration, parameters for filter are accepted as request params in url, at the moment: min-length, max-length, min-ascent, max-ascent, min-descent, max-descent

**relations**

Not very useful api at the moment, using for debug, will be removed or refactored in the future.

- GET */route/{id}/relation* - get relations for the route by route id


