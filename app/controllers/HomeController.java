package controllers;

/**
 * Created by ashish.pushp  on 21/6/17.
 */

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import play.Logger;
import play.libs.Json;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.concurrent.CompletableFuture;


 /*
        Json I am using to as post payload.
             {
                  "name": "Mercedes-Benz AMG GT",
                  "colour": "white",
                  "topSpeed": 280
             }
 */

//Using Java-8 Future Responses and promises, for making the application more concurrent.
//This is a sample class which demonstrates simple mongoDB operations and calls.
//This can be made more complex and modular but here, I have kept it simple enough for everyone to understand.
public class HomeController extends Controller {

    //creating an explicit executor.
    @Inject
    HttpExecutionContext ec;
    /*
     Mongo DB must be installed on local machine in order to this or you can pass the IP of machine on which you installed
     Mongo at the place of localhost.
     If your application is password protected
     Please see this example ::  https://stackoverflow.com/questions/34414406/connection-to-mongodb-with-authentication-fails/41380269#41380269
     Please do give a up vote if you like it.
    */

    private MongoClient mongo = new MongoClient("localhost", 27017);
    private MongoDatabase mongoDatabase = mongo.getDatabase("AwesomeDB");
    private MongoCollection<Document> collection;

    public HomeController() {
        this.collection = mongoDatabase.getCollection("CarsAreAwesome");
    }


    //This method is a post method which expects a json payload, and saves the json into MongoDB collection.
    public CompletableFuture<Result> postCarInfo() {

        return CompletableFuture.supplyAsync(() -> {
            //getting the payload from request body.
            JsonNode requestBody = request().body().asJson();
            Logger.info(requestBody.toString());
            //creating a document object for inserting into DB.
            Document document = Document.parse(Json.asciiStringify(requestBody));
            //inserting document into DB.
            collection.insertOne(new Document(document));
            //returning the same payload what we received.
            //we can also return some String message or some status code.
            return ok(requestBody).as("application/json");
        }, ec.current());

    }

    //This method is a get method which get all the drecords from the MongoDB collection and returns the json payload.
    public CompletableFuture<Result> getAllCarInfo() {

        return CompletableFuture.supplyAsync(() -> {
            // Getting all the records from MongoDB Collection and storing it into an Iterable Object.
            FindIterable<Document> allCars = collection.find();
            //Converting FindIterable<Document> object to JsonNode.
            JsonNode jsonNode = Json.toJson(allCars);
            Logger.info("List of Cars ::: {}", jsonNode.toString());
            //Converting the JsonNode Object to string and returning the same.
            return ok(Json.asciiStringify(jsonNode)).as("application/json");
        }, ec.current());

    }


    public CompletableFuture<Result> getCarInfo(String modelName){

        return CompletableFuture.supplyAsync(() -> {
            // Getting all the records from MongoDB Collection and storing it into an Iterable Object.
            FindIterable<Document> allCars = collection.find(new Document("name", modelName));
            //Converting FindIterable<Document> object to JsonNode.
            JsonNode jsonNode = Json.toJson(allCars);
            Logger.info("List of Cars ::: {}", jsonNode.toString());
            //Converting the JsonNode Object to string and returning the same.
            return ok(Json.asciiStringify(jsonNode)).as("application/json");
        }, ec.current());
    }


    public CompletableFuture<Result> deleteCarInfo(String modelName){

        return CompletableFuture.supplyAsync(() -> {
            // Deleting all the records from MongoDB Collection using the name filter.
            DeleteResult deleteResult = collection.deleteMany(new Document("name", modelName));
            return ok(deleteResult.getDeletedCount() + " records deleted...!").as("application/json");
        }, ec.current());
    }

    public CompletableFuture<Result> updateCarInfo(String oldModelName, String newModelName){

        return CompletableFuture.supplyAsync(() -> {
            // Updating all the records from MongoDB Collection using the oldModelName filter to newModelName.
            UpdateResult updateResult = collection.updateMany(new Document("name", oldModelName), new Document("name", newModelName));
            return ok(updateResult.getModifiedCount() + " records deleted...!").as("application/json");
        }, ec.current());
    }

}
