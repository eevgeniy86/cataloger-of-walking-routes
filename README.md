# cataloger-of-walking-routes

## Introduction
This is my first java/SpringBoot training project. The creation of this project does not set any financial goals nor does it aim to offer innovative solutions to existing technical problems. My primary goal when working on this project is to gain practical experience in java and learn more about the programming language and its related libraries and frameworks.

Here is the scope of desired functionality that I try to follow. During development, I maintain a backlog and documentation, trying to keep notes on the developed functionality and future plans.
At "release" I would like to see a service that could potentially be deployed on a server and hosted for use over the Internet.

I realize that, at least in its current form, the project is hardly be of practical interest to anyone but me, but nevertheless, I would be grateful for any constructive comments and suggestions.

In its initial form, the service provides a rest api for interacting with it, in the future I would like to make a simple web interface.


## Description
The service works with users walking routes. The main functionality of the service is assumed to be as follows:
1. Saving the user route plan (a sequence of points on the map) built in one of the popular public mapping services (google.Maps, yandex.Maps, 2gis, etc.) by transferring its hyperlink
2. Automatic calculation and saving of the main parameters of the transmitted route, which are important for the user's choice. The calculation uses data from various open-source mapping services:
- the total length of the route - according to data from the Open Source Routing Machine (OSRM) service (implementations jawg.io )
- information about public transport stops and their type near the route endpoints - according to the data from OpenStreetMaps (OSM) service
- the distance between route endpoints and the nearest public transport stations, and the distance between route endpoints (to understand whether this route can be considered a loop) - according to OSRM data
- total elevation gain and loss on the route - according to OSRM data
- regions through which the route passes (only an idea for the future)
- etc.
3. Providing a list of suitable routes, that satisfy the filtering parameters user set
4. Constructing and providing the user a hyperlink on mapping service (google.Maps, yandex.Maps, 2gis, etc.) for the requested route
5. Functioning of the service as a social network for walkers - the possibility of user registration and authorization, route exchange, ratings and comments (plans for the distant future)

## Done now
1. Saving of a route from url (only yandex) via rest api
2. Scheduled calculation of route length using data from OSRM
3. Scheduled retrieval of public transport stops nearest to route endpoints 
4. Rest api for routes
5. Integrational tests for routes (positive scenarios only)

## Future plans
1. Scheduled calculation of the total ascent and descent height on the route - according to OSRM data
2. Scheduled retrieval of distances from route endpoints and public transport stops
3. Maintaining urls for different mapping services (google, 2gis, etc.)
4. Refactoring the rest api to full functionality
5. More integration and unit tests for all functionality
6. Global code refactoring
7. Multiple users functionality:
- Authorization and roles
- User profiles
- Commenting, ratings, compilations of routes
8. Web-interfaces
9. etc.

## Specification (actual)
### DB schema
![DB scheme](https://github.com/eevgeniy86/cataloger-of-walking-routes/blob/main/png/cataloger-of-walking-routes%20DB.png)

[Source](https://app.diagrams.net/#G1bQZYkkVOpQfT3XxWXYR6dxSE6pfxUY9s#%7B"pageId"%3A"z3W7p9jmMmL0QTRQeyUE"%7D)

**route** - the main table for saved routes with basic information

**waypoint** - a sequence of points on the map defined by the user in route plan

**station** - information about public transport stops near route endpoints, obtained from OSM

**point** - the geographical coordinates of waypoints and stations

**relation** - describes useful relations for route waypoints - distances between route endpoints and nearest public transport stations, distances between the route endpoints

### Rest api

**routes**

- POST */route/url* - save a route from mapping service url (at the moment yandex.Maps only)
- POST */route* - save route from json-object
- GET */route/{id}* - get route by id as json-object
- GET */route/{id}/url* -  get route url for a mapping service (at the moment yandex.Maps only)
- GET */route* - get routes with filtration, parameters for filter are accepted as request params in the url, currently supported: min-length, max-length, min-ascent, max-ascent, min-descent, max-descent

**relations**

Not very useful api at the moment, used for debugging, will be removed or refactored in the future.

- GET */route/{id}/relation* - get relations for the route by route id




