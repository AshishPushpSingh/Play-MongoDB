package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.gridfs.GridFS;
import org.bson.Document;
import play.Logger;
import play.libs.Json;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Controller;
import play.mvc.Http.MultipartFormData;
import play.mvc.Result;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class HomeController extends Controller {

    @Inject
    HttpExecutionContext ec;
    private MongoClient mongo = new MongoClient("localhost", 27017);
    private MongoDatabase mongoDatabase = mongo.getDatabase("AwesomeDB");
    private MongoCollection<Document> collection;
    private GridFS attachmentBucket;

    public HomeController() {
        this.collection = mongoDatabase.getCollection("CarsAreAwesome");
        this.attachmentBucket = new GridFS(mongo.getDB("AwesomeDB"), "FileBucket");
    }

    public CompletableFuture<Result> postCarInfo() {

        return CompletableFuture.supplyAsync(() -> {
            JsonNode requestBody = request().body().asJson();
            Logger.info(requestBody.toString());
            Document document = Document.parse(Json.asciiStringify(requestBody));
            collection.insertOne(new Document(document));
            return ok(requestBody);
        }, ec.current());

    }

    public CompletableFuture<Result> getAllCarInfo() {

        return CompletableFuture.supplyAsync(() -> {
            FindIterable<Document> allCars = collection.find();
            System.out.println(allCars);
            JsonNode jsonNode = Json.toJson(allCars);
            return ok(Json.asciiStringify(jsonNode)).as("application/json");
        }, ec.current());

    }

    public CompletableFuture<Result> postFile() throws FileNotFoundException {

        return CompletableFuture.supplyAsync(() -> {
            MultipartFormData body = request().body().asMultipartFormData();
            List<MultipartFormData.FilePart> filePartList = body.getFiles();
            filePartList.forEach(filePart -> {
                File file = (File) filePart.getFile();
                try {
                    attachmentBucket.createFile(new FileInputStream(file), filePart.getFilename(), true).save();
                    Logger.info("Saved FileName : {} File : {} ", filePart.getFilename(), file);
                } catch (FileNotFoundException e) {
                    Logger.info("Error while saving the file into GridFS fileBucket ", e);
                }
            });
            return ok("Saved " + filePartList.size() + " files.");
        }, ec.current());
    }

    public Result index() {
        return ok("Your new application is ready.");
    }

}
