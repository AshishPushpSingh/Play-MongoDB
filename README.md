This is a sample java project in which we have performed all the CRUD operation on Json Data using REST endpoints.

We have used MongoDB as DataBase in this experiment. MongoDB is a free and open-source cross-platform document-oriented database
program. Classified as a NoSQL database program, MongoDB uses JSON-like documents with schemas

We have also performed file uploads using various methods supported by Play Framework.
We have also explored mongo GRIDFS which is mainly used to save files into MongoDB.

Endpoints:-
-----------------------------------------------------------------------------------------------------------------
----------------------------------------------------------------------------------------------------------------
Mongo CRUD operations
----------------------------------------------------------------------------------------------------------------

POST     /postcar                               controllers.HomeController.postCarInfo

GET      /getallcar                             controllers.HomeController.getAllCarInfo
GET      /getcar/:modelName                     controllers.HomeController.getCarInfo(modelName)
GET      /deletecar/:modelName                  controllers.HomeController.deleteCarInfo(modelName)
GET      /updatecar/:oldModelName/:newColour    controllers.HomeController.updateCarInfo(oldModelName, newColour)

----------------------------------------------------------------------------------------------------------------
Mongo GridFS(Files) related endpoints.
----------------------------------------------------------------------------------------------------------------

POST     /postfile                              controllers.MongoGRID.postSingleFileUsingKey
POST     /postmultiplefiles                     controllers.MongoGRID.postMultipleFiles
POST     /postFileAsRaw                         controllers.MongoGRID.postMultipleFiles

GET     /getfile/:fileName                      controllers.MongoGRID.getFile(fileName)
GET     /deletefile/:fileName                   controllers.MongoGRID.deleteFile(fileName)

----------------------------------------------------------------------------------------------------------------

## Components

- Module.java:

  Shows how to use Guice to bind all the components needed by your application.

- Counter.java:

  An example of a component that contains state, in this case a simple counter.

- ApplicationTimer.java:

  An example of a component that starts when the application starts and stops
  when the application stops.

## Filters

- Filters.java:

  Creates the list of HTTP filters used by your application.

- ExampleFilter.java

  A simple filter that adds a header to every response.


                                                                                                #Ashish Pushp