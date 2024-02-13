package com.db.repository;

import com.db.entity.Address;
import com.db.entity.Company;
import com.db.entity.Geo;
import com.db.entity.User;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

public class UserRepository {

    private final MongoCollection<Document> usersCollection;

    public UserRepository(MongoDatabase database) {
        this.usersCollection = database.getCollection("users");
    }

    public void insertUser(User user) {
        usersCollection.insertOne(convertToDocument(user));
    }

    public List<User> getUsersWithZipcodeEndingWith49() {
        List<User> users = new ArrayList<>();
        usersCollection.find(Filters.regex("address.zipcode", "4$|9$"))
                .map(this::convertToUser)
                .into(users);
        return users;
    }

    public List<User> getUsersWithNegativeLatitude() {
        List<User> users = new ArrayList<>();
        usersCollection.find(Filters.lt("address.geo.lat", "0"))
                .map(this::convertToUser)
                .into(users);
        return users;
    }

    public List<User> getUsersWithWebsiteEndingWithCom() {
        List<User> users = new ArrayList<>();
        usersCollection.find(Filters.regex("website", ".com$"))
                .map(this::convertToUser)
                .into(users);
        return users;
    }

    public void updateUserFieldsById(String userId, String newCatchPhrase, String newLng) {
        if (ObjectId.isValid(userId)) {
            usersCollection.updateOne(Filters.eq("_id", new ObjectId(userId)),
                    new Document("$set", new Document("company.catchPhrase", newCatchPhrase)
                            .append("address.geo.lng", newLng)),
                    new UpdateOptions().upsert(true));
            System.out.println("User with id " + userId + " updated successfully.");
        } else {
            System.out.println("Invalid ObjectId format for userId: " + userId);
        }
    }

    private Document convertToDocument(User user) {
        Document document = new Document();
        document.append("name", user.getName());
        document.append("username", user.getUsername());
        document.append("email", user.getEmail());
        document.append("address", convertAddressToDocument(user.getAddress()));
        document.append("phone", user.getPhone());
        document.append("website", user.getWebsite());
        document.append("company", convertCompanyToDocument(user.getCompany()));
        return document;
    }

    private Document convertAddressToDocument(Address address) {
        Document document = new Document();
        document.append("street", address.getStreet());
        document.append("suite", address.getSuite());
        document.append("city", address.getCity());
        document.append("zipcode", address.getZipcode());
        document.append("geo", convertGeoToDocument(address.getGeo()));
        return document;
    }

    private Document convertGeoToDocument(Geo geo) {
        Document document = new Document();
        document.append("lat", geo.getLat());
        document.append("lng", geo.getLng());
        return document;
    }

    private Document convertCompanyToDocument(Company company) {
        Document document = new Document();
        document.append("name", company.getName());
        document.append("catchPhrase", company.getCatchPhrase());
        document.append("bs", company.getBs());
        return document;
    }

    private User convertToUser(Document document) {
        String name = document.getString("name");
        String username = document.getString("username");
        String email = document.getString("email");
        Address address = convertToAddress(document.get("address", Document.class));
        String phone = document.getString("phone");
        String website = document.getString("website");
        Company company = convertToCompany(document.get("company", Document.class));

        User user = new User();
        user.setName(name);
        user.setUsername(username);
        user.setEmail(email);
        user.setAddress(address);
        user.setPhone(phone);
        user.setWebsite(website);
        user.setCompany(company);

        return user;
    }

    private Address convertToAddress(Document document) {
        String street = document.getString("street");
        String suite = document.getString("suite");
        String city = document.getString("city");
        String zipcode = document.getString("zipcode");
        Geo geo = convertToGeo(document.get("geo", Document.class));

        Address address = new Address();
        address.setStreet(street);
        address.setSuite(suite);
        address.setCity(city);
        address.setZipcode(zipcode);
        address.setGeo(geo);

        return address;
    }

    private Geo convertToGeo(Document document) {
        String lat = document.getString("lat");
        String lng = document.getString("lng");

        Geo geo = new Geo();
        geo.setLat(lat);
        geo.setLng(lng);

        return geo;
    }

    private Company convertToCompany(Document document) {
        String name = document.getString("name");
        String catchPhrase = document.getString("catchPhrase");
        String bs = document.getString("bs");

        Company company = new Company();
        company.setName(name);
        company.setCatchPhrase(catchPhrase);
        company.setBs(bs);

        return company;
    }
}
