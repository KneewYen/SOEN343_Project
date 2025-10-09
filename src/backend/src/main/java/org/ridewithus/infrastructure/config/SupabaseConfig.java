package org.ridewithus.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "spring.supabase")
public class SupabaseConfig {
    
    private String url;
    private String serviceRoleKey;
    private String anonKey;
    
    // Getters and Setters
    public String getUrl() {
        return url;
    }
    
    public void setUrl(String url) {
        this.url = url;
    }
    
    public String getServiceRoleKey() {
        return serviceRoleKey;
    }
    
    public void setServiceRoleKey(String serviceRoleKey) {
        this.serviceRoleKey = serviceRoleKey;
    }
    
    public String getAnonKey() {
        return anonKey;
    }
    
    public void setAnonKey(String anonKey) {
        this.anonKey = anonKey;
    }
}

