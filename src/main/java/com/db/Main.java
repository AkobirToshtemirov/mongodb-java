package com.db;

import com.db.entity.Address;
import com.db.entity.Company;
import com.db.entity.Geo;
import com.db.entity.User;
import com.db.repository.UserRepository;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        // Initialize MongoDB client
        MongoClient mongoClient = MongoClients.create("mongodb://127.0.0.1:27017/?directConnection=true");

        // Get database
        MongoDatabase db = mongoClient.getDatabase("java_db");

        // Create UserRepository instance
        UserRepository userRepository = new UserRepository(db);

        // Insert a new user
        User newUser = createDummyUser();
        userRepository.insertUser(newUser);
        System.out.println("Inserted user: " + newUser);

        // Find users with zipcode ending with 4 or 9
        List<User> usersWithZipcodeEndingWith49 = userRepository.getUsersWithZipcodeEndingWith49();
        System.out.println("Users with zipcode ending with 4 or 9:");
        for (User user : usersWithZipcodeEndingWith49) {
            System.out.println(user);
        }

        // Find users with negative latitude
        List<User> usersWithNegativeLatitude = userRepository.getUsersWithNegativeLatitude();
        System.out.println("Users with negative latitude:");
        for (User user : usersWithNegativeLatitude) {
            System.out.println(user);
        }

        // Find users with website ending with .com
        List<User> usersWithWebsiteEndingWithCom = userRepository.getUsersWithWebsiteEndingWithCom();
        System.out.println("Users with website ending with .com:");
        for (User user : usersWithWebsiteEndingWithCom) {
            System.out.println(user);
        }

        // Update user's company catchPhrase and address geo lng
        String userId = "objectId"; // Assuming objectId is a valid user id
        String newCatchPhrase = "NEW PHRASE";
        String newLng = "NEW VALUE";
        userRepository.updateUserFieldsById(userId, newCatchPhrase, newLng);
        System.out.println("User with id " + userId + " updated successfully.");
    }

    private static User createDummyUser() {
        User user = new User();
        user.setName("John Doe");
        user.setUsername("johndoe");
        user.setEmail("johndoe@example.com");

        Address address = new Address();
        address.setStreet("123 Main St");
        address.setSuite("Apt 101");
        address.setCity("Anytown");
        address.setZipcode("12345");
        Geo geo = new Geo();
        geo.setLat("12.3456");
        geo.setLng("-98.7654");
        address.setGeo(geo);
        user.setAddress(address);

        user.setPhone("123-456-7890");
        user.setWebsite("example.com");

        Company company = new Company();
        company.setName("Example Company");
        company.setCatchPhrase("Creating amazing solutions");
        company.setBs("Innovative solutions for all industries");
        user.setCompany(company);

        return user;
    }
}

