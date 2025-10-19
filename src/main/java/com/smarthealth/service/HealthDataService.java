package com.smarthealth.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Health Data Service
 * Handles symptom checking, first aid guidance, and Google Places API integration
 */
@Service
public class HealthDataService {

    @Value("${google.places.api.key}")
    private String googlePlacesApiKey;

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public HealthDataService() {
        this.webClient = WebClient.builder().build();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Check symptoms and provide recommendations
     */
    public Map<String, Object> checkSymptoms(List<String> symptoms) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // Load symptoms data from JSON file
            List<Map<String, Object>> symptomData = loadSymptomsData();
            
            // Find matching conditions
            List<Map<String, Object>> matchingConditions = new ArrayList<>();
            
            for (Map<String, Object> condition : symptomData) {
                @SuppressWarnings("unchecked")
                List<String> conditionSymptoms = (List<String>) condition.get("symptoms");
                
                int matchCount = 0;
                for (String symptom : symptoms) {
                    if (conditionSymptoms.stream().anyMatch(s -> s.toLowerCase().contains(symptom.toLowerCase()))) {
                        matchCount++;
                    }
                }
                
                if (matchCount > 0) {
                    Map<String, Object> match = new HashMap<>();
                    match.put("condition", condition.get("condition"));
                    match.put("matchCount", matchCount);
                    match.put("totalSymptoms", conditionSymptoms.size());
                    match.put("matchPercentage", (double) matchCount / conditionSymptoms.size() * 100);
                    match.put("recommendations", condition.get("recommendations"));
                    match.put("severity", condition.get("severity"));
                    matchingConditions.add(match);
                }
            }
            
            // Sort by match percentage
            matchingConditions.sort((a, b) -> Double.compare(
                (Double) b.get("matchPercentage"), 
                (Double) a.get("matchPercentage")
            ));
            
            result.put("symptoms", symptoms);
            result.put("matchingConditions", matchingConditions);
            result.put("totalMatches", matchingConditions.size());
            
            // Provide general recommendations
            result.put("generalRecommendations", getGeneralRecommendations(symptoms));
            
        } catch (Exception e) {
            result.put("error", "Failed to process symptoms: " + e.getMessage());
        }
        
        return result;
    }

    /**
     * Get first aid guidance
     */
    public Map<String, Object> getFirstAidGuidance(String emergencyType) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            List<Map<String, Object>> firstAidData = loadFirstAidData();
            
            for (Map<String, Object> guidance : firstAidData) {
                if (guidance.get("type").toString().toLowerCase().contains(emergencyType.toLowerCase())) {
                    result.put("type", guidance.get("type"));
                    result.put("steps", guidance.get("steps"));
                    result.put("warnings", guidance.get("warnings"));
                    result.put("whenToSeekHelp", guidance.get("whenToSeekHelp"));
                    return result;
                }
            }
            
            result.put("error", "No first aid guidance found for: " + emergencyType);
            
        } catch (Exception e) {
            result.put("error", "Failed to load first aid guidance: " + e.getMessage());
        }
        
        return result;
    }

    /**
     * Get all first aid types
     */
    public List<String> getAllFirstAidTypes() {
        try {
            List<Map<String, Object>> firstAidData = loadFirstAidData();
            return firstAidData.stream()
                    .map(data -> data.get("type").toString())
                    .toList();
        } catch (Exception e) {
            return List.of("CPR", "Burns", "Bleeding", "Choking", "Heart Attack", "Stroke");
        }
    }

    /**
     * Find nearby hospitals using Google Places API
     */
    public Map<String, Object> findNearbyHospitals(double latitude, double longitude, int radius) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            String url = String.format(
                "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=%f,%f&radius=%d&type=hospital&key=%s",
                latitude, longitude, radius, googlePlacesApiKey
            );
            
            JsonNode response = webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .block();
            
            if (response != null && response.has("results")) {
                List<Map<String, Object>> hospitals = new ArrayList<>();
                
                for (JsonNode place : response.get("results")) {
                    Map<String, Object> hospital = new HashMap<>();
                    hospital.put("name", place.get("name").asText());
                    hospital.put("placeId", place.get("place_id").asText());
                    
                    if (place.has("vicinity")) {
                        hospital.put("address", place.get("vicinity").asText());
                    }
                    
                    if (place.has("rating")) {
                        hospital.put("rating", place.get("rating").asDouble());
                    }
                    
                    if (place.has("geometry") && place.get("geometry").has("location")) {
                        JsonNode location = place.get("geometry").get("location");
                        hospital.put("latitude", location.get("lat").asDouble());
                        hospital.put("longitude", location.get("lng").asDouble());
                    }
                    
                    hospitals.add(hospital);
                }
                
                result.put("hospitals", hospitals);
                result.put("count", hospitals.size());
            } else {
                result.put("error", "No hospitals found in the specified area");
            }
            
        } catch (Exception e) {
            result.put("error", "Failed to fetch nearby hospitals: " + e.getMessage());
        }
        
        return result;
    }

    /**
     * Find nearby pharmacies using Google Places API
     */
    public Map<String, Object> findNearbyPharmacies(double latitude, double longitude, int radius) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            String url = String.format(
                "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=%f,%f&radius=%d&type=pharmacy&key=%s",
                latitude, longitude, radius, googlePlacesApiKey
            );
            
            JsonNode response = webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .block();
            
            if (response != null && response.has("results")) {
                List<Map<String, Object>> pharmacies = new ArrayList<>();
                
                for (JsonNode place : response.get("results")) {
                    Map<String, Object> pharmacy = new HashMap<>();
                    pharmacy.put("name", place.get("name").asText());
                    pharmacy.put("placeId", place.get("place_id").asText());
                    
                    if (place.has("vicinity")) {
                        pharmacy.put("address", place.get("vicinity").asText());
                    }
                    
                    if (place.has("rating")) {
                        pharmacy.put("rating", place.get("rating").asDouble());
                    }
                    
                    if (place.has("geometry") && place.get("geometry").has("location")) {
                        JsonNode location = place.get("geometry").get("location");
                        pharmacy.put("latitude", location.get("lat").asDouble());
                        pharmacy.put("longitude", location.get("lng").asDouble());
                    }
                    
                    pharmacies.add(pharmacy);
                }
                
                result.put("pharmacies", pharmacies);
                result.put("count", pharmacies.size());
            } else {
                result.put("error", "No pharmacies found in the specified area");
            }
            
        } catch (Exception e) {
            result.put("error", "Failed to fetch nearby pharmacies: " + e.getMessage());
        }
        
        return result;
    }

    /**
     * Load symptoms data from JSON file
     */
    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> loadSymptomsData() throws IOException {
        ClassPathResource resource = new ClassPathResource("static/data/symptoms.json");
        try (InputStream inputStream = resource.getInputStream()) {
            return objectMapper.readValue(inputStream, List.class);
        }
    }

    /**
     * Load first aid data from JSON file
     */
    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> loadFirstAidData() throws IOException {
        ClassPathResource resource = new ClassPathResource("static/data/firstaid.json");
        try (InputStream inputStream = resource.getInputStream()) {
            return objectMapper.readValue(inputStream, List.class);
        }
    }

    /**
     * Get general recommendations based on symptoms
     */
    private List<String> getGeneralRecommendations(List<String> symptoms) {
        List<String> recommendations = new ArrayList<>();
        
        recommendations.add("If symptoms persist or worsen, seek medical attention immediately.");
        recommendations.add("Keep track of your symptoms and their duration.");
        recommendations.add("Stay hydrated and get adequate rest.");
        
        if (symptoms.stream().anyMatch(s -> s.toLowerCase().contains("chest") || s.toLowerCase().contains("heart"))) {
            recommendations.add("⚠️ Chest pain or heart-related symptoms require immediate medical attention. Call emergency services.");
        }
        
        if (symptoms.stream().anyMatch(s -> s.toLowerCase().contains("breathing") || s.toLowerCase().contains("shortness"))) {
            recommendations.add("⚠️ Breathing difficulties require immediate medical attention. Call emergency services.");
        }
        
        if (symptoms.stream().anyMatch(s -> s.toLowerCase().contains("fever") || s.toLowerCase().contains("temperature"))) {
            recommendations.add("Monitor your temperature regularly and consider fever-reducing medications if appropriate.");
        }
        
        return recommendations;
    }
}
