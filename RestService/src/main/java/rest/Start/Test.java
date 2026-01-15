package rest.Start;

import org.springframework.web.client.RestClient;
import rest.Model.Proba;

public class Test {
    public static void main(String[] args) {
        String baseUrl = "http://localhost:1234/api/probas";
        RestClient restClient = RestClient.create();


        System.out.println("=== Test Create Proba ===");
        Proba proba = new Proba(null, "100m", "Freestyle");
        try {
            Integer id = restClient.post()
                    .uri(baseUrl)
                    .body(proba)
                    .retrieve()
                    .toEntity(Integer.class)
                    .getBody();
            System.out.println("Created Proba with ID: " + id);
        } catch (Exception e) {
            System.err.println("Error creating Proba: " + e.getMessage());
            throw e;
        }


        System.out.println("=== Test Find Proba by ID ===");
        try {
            Proba found = restClient.get()
                    .uri(baseUrl + "/{id}", 1)
                    .retrieve()
                    .toEntity(Proba.class)
                    .getBody();
            System.out.println("Found Proba: " + found.getDistanta() + " " + found.getStil());
        } catch (Exception e) {
            System.err.println("Error finding Proba: " + e.getMessage());
        }


        System.out.println("=== Test Find All Probas ===");
        try {
            Proba[] probas = restClient.get()
                    .uri(baseUrl)
                    .retrieve()
                    .toEntity(Proba[].class)
                    .getBody();
            System.out.println("Total Probas: " + probas.length);
            for (Proba p : probas) {
                System.out.println("- " + p.getDistanta() + " " + p.getStil());
            }
        } catch (Exception e) {
            System.err.println("Error finding all Probas: " + e.getMessage());
        }


        System.out.println("=== Test Update Proba ===");
        Proba updatedProba = new Proba(1, "200m", "Backstroke");
        try {
            Proba updated = restClient.put()
                    .uri(baseUrl + "/{id}", 1)
                    .body(updatedProba)
                    .retrieve()
                    .toEntity(Proba.class)
                    .getBody();
            System.out.println("Updated Proba to: " + updated.getDistanta() + " " + updated.getStil());
        } catch (Exception e) {
            System.err.println("Error updating Proba: " + e.getMessage());
        }


        System.out.println("=== Test Delete Proba ===");
        try {
            restClient.delete()
                    .uri(baseUrl + "/{id}", 1)
                    .retrieve()
                    .toBodilessEntity();
            System.out.println("Deleted Proba with ID: 1");
        } catch (Exception e) {
            System.err.println("Error deleting Proba: " + e.getMessage());
        }


        System.out.println("=== Test Verify Deletion ===");
        try {
            restClient.get().uri(baseUrl + "/{id}", 1).retrieve().toEntity(Proba.class);
            System.out.println("Unexpected: Proba still exists");
        } catch (Exception e) {
            System.out.println("Expected error (404): " + e.getMessage());
        }


        System.out.println("=== Test Invalid Input ===");
        Proba invalidProba = new Proba(null, "", "");
        System.out.println("Sending invalid Proba: " + invalidProba);
        try {
            restClient.post().uri(baseUrl).body(invalidProba).retrieve().toEntity(Integer.class);
            System.out.println("Unexpected: Invalid Proba was created");
        } catch (Exception e) {
            System.out.println("Expected error (400): " + e.getMessage());
        }
    }
}