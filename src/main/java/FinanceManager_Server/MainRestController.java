package FinanceManager_Server;


import FinanceManager_Server.Database.BudgetRepository;
import FinanceManager_Server.Database.CategoryRepository;
import FinanceManager_Server.Database.Entity.User;
import FinanceManager_Server.Database.TransactionRepository;
import FinanceManager_Server.Database.UserRepository;
import FinanceManager_Server.Services.AuthService;
import FinanceManager_Server.Services.EmailService;
import FinanceManager_Server.TransportableDataObjects.TokenData;
import io.jsonwebtoken.Claims;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Date;


@RestController
public class MainRestController {
    UserRepository userRepository;
    TransactionRepository transactionRepository;
    CategoryRepository categoryRepository;
    BudgetRepository budgetRepository;
    EmailService emailService;
    AuthService authService;

    /*STATUS:ACCEPTED, SC_CONTINUE, SC_CREATED ,SC_MULTI_STATUS, SC_NO_CONTENT, SC_NON_AUTHORITATIVE_INFORMATION ,	SC_OK ,
    SC_PARTIAL_CONTENT, SC_RESET_CONTENT

    */
    public MainRestController(UserRepository userRepository, TransactionRepository transactionRepository, CategoryRepository categoryRepository, BudgetRepository budgetRepository, EmailService emailService, AuthService authService) {
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
        this.categoryRepository = categoryRepository;
        this.budgetRepository = budgetRepository;
        this.emailService = emailService;
        this.authService = authService;
    }




    @PostMapping(value = "/register",  produces = "application/json")
    public ResponseEntity<String> register(@RequestParam(value = "email") String email, @RequestParam(value = "password") String password,
                                   @RequestParam(value = "lang", required = false, defaultValue = "ENGLISH") String lang){
        User user = userRepository.findByEmail(email);
        if(user == null){
            user = new User(email, password, lang, Boolean.FALSE, "", "");
            System.out.println("Saving " + user);
            userRepository.saveAndFlush(user);
        }else if (user.getVerified() == Boolean.TRUE){
            return new ResponseEntity<>("ALREADY_REGISTERED", HttpStatus.RESET_CONTENT);
        }
        String access_token = authService.createAccessToken(user.getId());
        String refresh_token = authService.createRefreshToken(user.getId());
        userRepository.updateTokens(user.getId(), access_token, refresh_token);
        String url = emailService.sendVerification(email, access_token);
        return new ResponseEntity<>(url, HttpStatus.OK);
        //FOR DEBUG purpose send back access token
    }

    @RequestMapping(value = "/verify", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<String> verify(@RequestParam String access_token){
        System.out.println("Verify initiated");
        if(authService.checkIsExpired(access_token)){
            return new ResponseEntity<>("Token expired",HttpStatus.RESET_CONTENT);
        }
        Claims claims = authService.getClaims(access_token);
        Long id = Long.parseLong(claims.getSubject());
        User user = userRepository.getById(id);
        if(user == null){
            return new ResponseEntity<>("User not found",HttpStatus.FORBIDDEN);
        }
        if(!user.getAccess_token().equals(access_token)){
            return new ResponseEntity<>("Token mismatch",HttpStatus.FORBIDDEN);
        }
        userRepository.setUserVerifiedField(user.getId(), Boolean.TRUE);
        return new ResponseEntity<>("Email verified", HttpStatus.OK);
    }

    @RequestMapping(value = "/login")
    public ResponseEntity<TokenData> login(@RequestParam(value = "email") String email, @RequestParam(value = "password") String password){
        User user = userRepository.findByEmail(email);
        if(user == null){
            return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
        }
        if(!user.getPassword().equals(password)){
            return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
        }
        if(user.getVerified() == Boolean.FALSE){
            return  new ResponseEntity<>(null, HttpStatus.CREATED);
        }
        String access = authService.createAccessToken(user.getId());
        String refresh = authService.createRefreshToken(user.getId());
        Date now = Date.from(Instant.now());

        TokenData tokenData = new TokenData(access, refresh, authService.getAccessTokenExpireDate(now),
                authService.getRefreshTokenExpireDate(now));
        return  new ResponseEntity<>(tokenData, HttpStatus.ACCEPTED);
    }

    @RequestMapping(value = "/clear")
    public ResponseEntity<String> clear(){

        userRepository.deleteAll();
        return new ResponseEntity<>("DELETED", HttpStatus.OK);
    }


}
