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
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.CompletableFuture;

//Using Java-8 Future Responses and promises, for making the application more concurrent.
//This is a sample class which demonstrates simple mongoDB calls for saving Files the form of GridFS.
//This can be made more complex and modular but here, I have kept it simple enough for everyone to understand.
public class MongoGRID extends Controller {

    @Inject
    HttpExecutionContext ec;
    private MongoClient mongo = new MongoClient("localhost", 27017);
    private GridFS fileBucket;

    //Creating a new GridFS collection in constructor.
    public MongoGRID() {
        this.fileBucket = new GridFS(mongo.getDB("AwesomeDB"), "FileBucket");
    }

    //This method expects MultiPart form data as input. We can post Multiple Files at a time.
    public CompletableFuture<Result> postMultipleFiles() {

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

    //This method expects MultiPart form data as input and with key as "file".
    public CompletableFuture<Result> postSingleFileUsingKey() {

        return CompletableFuture.supplyAsync(() -> {
            //Getting the multipart request.
            Http.MultipartFormData body = request().body().asMultipartFormData();
            //Getting List of files out from body using the key "file"
            Http.MultipartFormData.FilePart filePart = body.getFile("file");
            File file = (File) filePart.getFile();
            try {
                // Saving file into GridFS
                fileBucket.createFile(new FileInputStream(file), filePart.getFilename(), true).save();
            } catch (FileNotFoundException e) {
                Logger.info("Error while saving the file into GridFS fileBucket ", e);
            }
            return ok("Saved the File  " + filePart.getFilename() + ".");
        }, ec.current());
    }

    //This method expects raw file as input.
    public CompletableFuture<Result> postFileAsRaw() {

        return CompletableFuture.supplyAsync(() -> {
            //Getting the File as raw from request Object.
            File file = request().body().asRaw().asFile();
            try {
                // Saving file into GridFS
                fileBucket.createFile(new FileInputStream(file), file.getName(), true).save();
            } catch (FileNotFoundException e) {
                Logger.info("Error while saving the file into GridFS fileBucket ", e);
            }
            return ok("Saved the File  " + file.getName() + ".");
        }, ec.current());
    }

    //This method returns the passed file and returns in form of InputStream.
    public CompletableFuture<Result> getFile(String fileName) {

        return CompletableFuture.supplyAsync(() -> {
            //Getting the file from MongoDB.
            InputStream gridFSDBFileInputStream = fileBucket.findOne(fileName).getInputStream();
            return ok().sendInputStream(gridFSDBFileInputStream);
        }, ec.current());
    }

    //This method returns the passed file and returns in form of InputStream.
    public CompletableFuture<Result> deleteFile(String fileName) {
        return CompletableFuture.supplyAsync(() -> {
            //Deleting the file from MongoDB.
            fileBucket.remove(fileName);
            return ok("File Deleted SuccessFully");
        }, ec.current());
    }

}
