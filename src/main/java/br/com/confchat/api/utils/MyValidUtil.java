package br.com.confchat.api.utils;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.regex.Pattern;
@Component
public class MyValidUtil {
    public boolean validLogin(String login){
        Pattern pattern = Pattern.compile("^[0-9a-z.]{8,}$");
        if(login == null)
            return false;
        return pattern.matcher(login).matches();
    }
    public boolean validPassword(String password){
        Pattern pattern = Pattern.compile("^(?=.*[0-9])(?=.*[!@#$%^&*])[0-9a-zA-Z!@#$%^&* ]{15,}$");
        if(password == null)
            return false;
        return pattern.matcher(password).matches();
    }
    public boolean validBirthDay(LocalDate birthDay){
        var minimumAge = LocalDate.now().minusYears(18);
        if(birthDay.isBefore(minimumAge)){
            return true;
        }
        return false;
    }
    public boolean validName(String name){
        Pattern pattern = Pattern.compile("^[a-zA-Z]+ [a-zA-Z]+$");
        if(name == null)
            return false;
        return pattern.matcher(name).matches();
    }
    public boolean validEmail(String email){
        Pattern pattern = Pattern.compile("^[0-9a-zA-Z.]+@[a-zA-Z0-9]+\\.[a-zA-Z0-9.]+$");
        if(email == null)
            return false;
        return pattern.matcher(email).matches();
    }
}
