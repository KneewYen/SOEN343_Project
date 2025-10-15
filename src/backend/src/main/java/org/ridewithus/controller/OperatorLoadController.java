package org.ridewithus.controller;

import org.ridewithus.domain.entity.User;
import org.ridewithus.infrastructure.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.util.List;

@RestController
@RequestMapping("/import")
public class OperatorLoadController {
    
    @Autowired
    private UserRepository userRepository;

    @PostMapping("/operators")
    public String importOperators(){
        try{
            ObjectMapper mapper = new ObjectMapper();
            File file = new File("src/main/resources/operators.json");

            List<User> operators = mapper.readValue(file, new TypeReference<List<User>>(){});

            for(User op: operators){
                userRepository.save(op);
            }
            
            return "success";
        }
        catch(Exception e){
            System.err.println("An error occurred: " + e.getMessage());
            return "failure" ;
        }

    }
}
