package controllers;

/**
 * Created by ashish.pushp  on 21/6/17.
 */

import com.google.inject.Inject;
import com.mongodb.MongoClient;
import com.mongodb.gridfs.GridFS;
import play.Logger;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

//Using Java-8 Future Responses and promises, for making the application more concurrent.
//This is a sample class which demonstrates simple mongoDB calls for saving Files the form of GridFS.
//This can be made more complex and modular but here, I have kept it simple enough for everyone to understand.
public class MongoGRID extends Controller {

    @Inject
    private HttpExecutionContext ec;
    private MongoClient mongo = new MongoClient("localhost", 27017);
    private GridFS fileBucket;

    //Creating a new GridFS collection in constructor.
    public MongoGRID() {
        this.fileBucket = new GridFS(mongo.getDB("AwesomeDB"), "FileBucket");
    }

    public CompletableFuture<Result> postFile() throws FileNotFoundException {

        return CompletableFuture.supplyAsync(() -> {
            //Getting the multipart request.
            Http.MultipartFormData body = request().body().asMultipartFormData();
            //Getting List of files out from body
            List<Http.MultipartFormData.FilePart> filePartList = body.getFiles();
            //Iterating through each file and saving it into DB.
            filePartList.forEach(filePart -> {
                File file = (File) filePart.getFile();
                try {
                    // Saving file into GridFS
                    fileBucket.createFile(new FileInputStream(file), filePart.getFilename(), true).save();
                    Logger.info("Saved FileName : {} File : {} ", filePart.getFilename(), file);
                } catch (FileNotFoundException e) {
                    Logger.info("Error while saving the file into GridFS fileBucket ", e);
                }
            });
            return ok("Saved " + filePartList.size() + " files.");
        }, ec.current());
    }

}
