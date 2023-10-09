package com.bookit.bookit.service.user;

import com.bookit.bookit.dto.UpdateUserDTO;
import com.bookit.bookit.entity.user.User;
import com.bookit.bookit.repository.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

 /*   private static UserRepository userRepository;

    //Constructor
    @Autowired
    public UserService(UserRepository userRepository) {
        UserService.userRepository = userRepository;
    }

    //Service för uppdatering av UserEntity via UpdateUserDTOs instance requestDTO där det anges username och password

    public void UpdateUserNameAndPassword(Integer userId, UpdateUserDTO requestDTO) {
        Optional<User> user = userRepository.findById(userId); //Skapa et playerEntity objekt och sök spelare via ovan uuid och ange värdet till objektet
        if (user.isPresent()) {  //Om spelaren motsvarande uuid-n finns
            User userEntity = user.get();  //...hämta den
            userEntity.setEmail(requestDTO.getEmail()); //Lägger till username och password
            userEntity.setPassword(requestDTO.getPassword());
            userRepository.save(userEntity); //Spara det uppdaterade entiteten via repository i databasen
        }
    }*/

}


