package org.ridewithus.infrastructure.config;

import org.springframework.web.reactive.function.client.WebClient;

// SINGLETON  client to access supabase API and services such as Authentication and Storage (file upload)
public class SupabaseClient {
    private static SupabaseClient instance = null;
    private final WebClient webClient;

    // currently using public api key. Option to switch to service version which grants full access but CANNOT be leaked !
    private SupabaseClient(){
        webClient = WebClient.builder()
            .baseUrl("https://hpgawptoeiwdajfrqvvz.supabase.co/rest/v1")
            .defaultHeader("apikey", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImhwZ2F3cHRvZWl3ZGFqZnJxdnZ6Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NTk5MDEwOTYsImV4cCI6MjA3NTQ3NzA5Nn0.xpD8QznbSLADZU7kUyDFn_4cEJvGeCrG9fKO-LrXKJY")
            .defaultHeader("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImhwZ2F3cHRvZWl3ZGFqZnJxdnZ6Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NTk5MDEwOTYsImV4cCI6MjA3NTQ3NzA5Nn0.xpD8QznbSLADZU7kUyDFn_4cEJvGeCrG9fKO-LrXKJY")
            .build();
    }

    public static SupabaseClient getInstance(){
        if(instance == null){
            instance = new SupabaseClient();
        }
        return instance ;
    }

    public WebClient getWebClient(){
        return this.webClient;
    }
}
