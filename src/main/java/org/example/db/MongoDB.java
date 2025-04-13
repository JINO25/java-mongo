package org.example.db;

import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.conversions.Bson;

import javax.print.Doc;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class MongoDB {
    String urlCon ="mongodb://localhost:27017";
    boolean collectionExists=false;
    boolean databaseExists = false;

    private MongoClient mongoClient;
    private MongoDatabase database;

    private String databaseName = "mongo_java";
    private String collection = "Student";

    public MongoDB() {
        this.connect();
    }

    public void connect(){
        try{
            mongoClient = MongoClients.create(urlCon);
            System.out.println("Connected successfully!");

            MongoIterable<Document> listDatabasesIterable = mongoClient.listDatabases();
            for(Document iterable : listDatabasesIterable){
                String name = iterable.getString("name");
                if(name.equals(databaseName)){
                    databaseExists=true;
                    break;
                }
            }

            if(!databaseExists){
                database= mongoClient.getDatabase(databaseName);
                database.getCollection("initCollection").insertOne(new Document("init","created"));
                System.out.println("database '"+databaseName+"' created.");
            }else{
                System.out.println("database '"+databaseName+"'  already exists.");
            }

            database = mongoClient.getDatabase(databaseName);

            ListCollectionsIterable<Document> list= database.listCollections();
            for (Document doc : list) {
                String name = doc.getString("name");
                if (name.equals(collection)) {
                    collectionExists = true;
                    break;
                }
            }

            if (!collectionExists) {
                database.createCollection(collection);
                System.out.println("Collection '"+collection+"' created.");
            } else {
                System.out.println("Collection '"+collection+"' already exists.");
            }

        }catch (Exception e){
            System.out.println("Error: "+e);
        }
    }

    public void findAll(){
        MongoCollection<Document> collection = database.getCollection(this.collection);
        Bson projectionFileds = Projections.fields(Projections.excludeId());
        FindIterable<Document> documents = collection.find().projection(projectionFileds);
        int i=1;
        for(Document doc : documents){

            System.out.println("------------User "+i+"------------");
            System.out.printf("Name          : %s%n", doc.getString("name"));
            System.out.printf("Email         : %s%n", doc.getString("email"));
            System.out.printf("Major         : %s%n", doc.getString("major"));
            System.out.printf("Address       : %s%n", doc.getString("address"));
            System.out.printf("Phone         : %s%n", doc.getString("phone"));
            System.out.printf("Date of Birth : %s%n", doc.getString("dateofBirth"));
            System.out.println("Curriculums:");
            System.out.println("----------------------------------------");

            List<Document> curriculums = (List<Document>) doc.get("curriculums");
            if (curriculums != null && !curriculums.isEmpty()) {
                for (Document subject : curriculums) {
                    System.out.printf("- Subject: %5s | Score: %.1f%n",
                            subject.getString("subject"), subject.getDouble("point"));
                }
            }
            i++;
        }
    }

    public void createUser() {
        Scanner scanner = new Scanner(System.in);
        String name, email, major, address, date, phone;
        System.out.print("Enter name: ");
        name = scanner.nextLine();
        System.out.print("Enter email: ");
        email = scanner.nextLine();System.out.print("Enter your major: ");
        major = scanner.nextLine();
        System.out.print("Enter date of birht (yyyy/mm/dd): ");
        date = scanner.nextLine();
        System.out.print("Enter your address: ");
        address = scanner.nextLine();
        System.out.print("Enter your phone: ");
        phone = scanner.nextLine();

        MongoCollection<Document> collection = database.getCollection(this.collection);
        Document doc = collection.find(Filters.eq("email", email)).first();

        if (doc == null) {
            List<Document> resultList = new ArrayList<>();

            // Nhập số lượng môn học
            System.out.print("Enter the number of subject: ");
            int numSubjects = scanner.nextInt();
            scanner.nextLine();

            // Nhập thông tin từng môn học
            for (int i = 0; i < numSubjects; i++) {
                System.out.print("Enter the name of subject " + (i + 1) + ": ");
                String subject = scanner.nextLine();

                System.out.print("Enter the score of " + subject + ": ");
                double score = scanner.nextDouble();
                scanner.nextLine();

                resultList.add(new Document("subject", subject).append("point", score));
            }

            Document user = new Document("name", name)
                    .append("email", email)
                    .append("major",major)
                    .append("dateofBirth", date)
                    .append("address", address)
                    .append("phone", phone)
                    .append("curriculums", resultList);

            collection.insertOne(user);
            System.out.println("Users are inserted successful!");
        } else {
            System.out.println("Email existed!");
        }
    }

    public void findUserByEmail(String email){
        MongoCollection<Document> collection = database.getCollection(this.collection);
        Document doc = collection.find(Filters.eq("email",email)).projection(Projections.excludeId()).first();
        if (doc != null) {
            System.out.println("========================================");
            System.out.println("User Information");
            System.out.println("========================================");
            System.out.printf("Name          : %s%n", doc.getString("name"));
            System.out.printf("Email         : %s%n", doc.getString("email"));
            System.out.printf("Major         : %s%n", doc.getString("major"));
            System.out.printf("Address       : %s%n", doc.getString("address"));
            System.out.printf("Phone         : %s%n", doc.getString("phone"));
            System.out.printf("Date of Birth : %s%n", doc.getString("dateofBirth"));
            System.out.println("----------------------------------------");
            System.out.println("Curriculums:");
            System.out.println("----------------------------------------");

            List<Document> curriculums = (List<Document>) doc.get("curriculums");
            if (curriculums != null && !curriculums.isEmpty()) {
                for (Document subject : curriculums) {
                    System.out.printf("- Subject: %5s | Score: %.1f%n",
                            subject.getString("subject"), subject.getDouble("point"));
                }
            } else {
                System.out.println("  No subjects found.");
            }
            System.out.println("========================================");
        } else {
            System.out.println("User not found.");
        }
    }

    public void updateUser(String email, String name, String major, String date, String address, String phone, String subject, double score) {
        MongoCollection<Document> collection = database.getCollection(this.collection);
        Document doc = collection.find(Filters.eq("email", email)).first();

        if (doc == null) {
            System.out.println("User not found");
        } else {
            Document updateFields = new Document();

            if (name != null && !name.isEmpty()) {
                updateFields.append("name", name);
            }
            if (date != null && !date.isEmpty()) {
                updateFields.append("dateofBirth", date);
            }
            if (major != null && !major.isEmpty()) {
                updateFields.append("major", major  );
            }
            if (address != null && !address.isEmpty()) {
                updateFields.append("address", address);
            }
            if (phone != null && !phone.isEmpty()) {
                updateFields.append("phone", phone);
            }

            if(subject != null && !subject.isEmpty() && score > 0){
                boolean updateSubject = false;
                List<Document> list = (List<Document>) doc.get("curriculums");
                if(list != null){
                    for(Document document : list){
                        if(document.getString("subject").equals(subject)){
                                document.put("point",score);
                                updateSubject = true;
                        }
                    }
                }

                if (!updateSubject) {
                    System.out.println("Subject doesn't exist!");
                    return;
                }

                updateFields.append("curriculums", list);

            }

            if (!updateFields.isEmpty()) {
                collection.updateOne(Filters.eq("email", email), new Document("$set", updateFields));
                System.out.println("User updated successfully!");
            } else {
                System.out.println("No changes were provided.");
            }
        }
    }

    public void insertSubject(String email){
        Scanner scanner = new Scanner(System.in);
        MongoCollection<Document> collection = database.getCollection(this.collection);
        Document doc = collection.find(Filters.eq("email",email)).first();
        if(doc == null){
            System.out.println("User not found");
        }else{
            int number;

            List<Document> list = new ArrayList<>();
            System.out.print("Enter the number of subject: ");
            number=scanner.nextInt();
            for(int i=0;i<number;i++){
                System.out.print("Enter the name of subject "+(i+1)+": ");
                scanner.nextLine();
                String subject=scanner.nextLine();
                System.out.print("Enter the score of "+subject+" : ");
                Double score=scanner.nextDouble();
                list.add(new Document("subject",subject).append("point",score));
            }

            List<Document> existingSubjectOfUser = (List<Document>) doc.get("curriculums");
                if(existingSubjectOfUser == null){
                    existingSubjectOfUser = new ArrayList<>();
                }
                existingSubjectOfUser.addAll(list);
            collection.updateOne(Filters.eq("email",email), Updates.set("curriculums",existingSubjectOfUser));
            System.out.println("Subject inserted successfully!");
        }
    }

    public void deleteSubject(String email){
        Scanner scanner = new Scanner(System.in);
        MongoCollection<Document> collection = database.getCollection(this.collection);
        Document doc = collection.find(Filters.eq("email",email)).first();
        if(doc == null){
            System.out.println("User not found");
        }else{
            System.out.print("Enter the subject to subject: ");
            String subject=scanner.nextLine();
            collection.updateOne(Filters.eq("email",email), Updates.pull("curriculums",new Document("subject",subject)));
            System.out.println("Delete successful subject!");
        }
    }

    public void deleteUser(String email){
        MongoCollection<Document> collection = database.getCollection(this.collection);
        Document doc = collection.find(Filters.eq("email", email)).first();
        if(doc == null){
            System.out.println("User not found");
        }else{
            collection.deleteOne(Filters.eq("email",email));
            System.out.println("Delete successfully!");
        }
    }

}
