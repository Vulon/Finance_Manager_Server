package FinanceManager_Server.RestControllers;


import FinanceManager_Server.Database.Repositories.BudgetRepository;
import FinanceManager_Server.Database.Repositories.CategoryRepository;
import FinanceManager_Server.Database.Entity.User;
import FinanceManager_Server.Database.Repositories.TransactionRepository;
import FinanceManager_Server.Database.Repositories.UserRepository;
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
public class AuthRestController {
    UserRepository userRepository;
    TransactionRepository transactionRepository;
    CategoryRepository categoryRepository;
    BudgetRepository budgetRepository;
    AuthService authService;
    EmailService emailService;

    /*STATUS:ACCEPTED, SC_CONTINUE, SC_CREATED ,SC_MULTI_STATUS, SC_NO_CONTENT, SC_NON_AUTHORITATIVE_INFORMATION ,	SC_OK ,
    SC_PARTIAL_CONTENT, SC_RESET_CONTENT

    */
    public AuthRestController(UserRepository userRepository, TransactionRepository transactionRepository, CategoryRepository categoryRepository, BudgetRepository budgetRepository, EmailService emailService, AuthService authService) {
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
            return new ResponseEntity<>("ALREADY_REGISTERED", HttpStatus.PRECONDITION_FAILED);
        }
        Date now = Date.from(Instant.now());
        String access_token = authService.createAccessToken(user.getId(), now);
        String refresh_token = authService.createRefreshToken(user.getId(), now);
        userRepository.updateTokens(user.getId(), access_token, refresh_token);
        String url = emailService.sendVerification(email, access_token);
        return new ResponseEntity<>(url, HttpStatus.OK);
        //FOR DEBUG purpose send back access token
    }

    @RequestMapping(value = "/verify", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<String> verify(@RequestParam String access_token){
        System.out.println("Verify initiated");
        if(authService.tokenHasErrors(access_token, true)){
            return new ResponseEntity<>("Token incorrect",HttpStatus.UNAUTHORIZED);
        }
        Claims claims = authService.getClaims(access_token);
        Long id = Long.parseLong(claims.getSubject());
        User user = userRepository.getById(id);
        if(user == null){
            return new ResponseEntity<>("User not found",HttpStatus.NOT_ACCEPTABLE);
        }
        if(!user.getAccess_token().equals(access_token)){
            return new ResponseEntity<>("Token mismatch",HttpStatus.UNAUTHORIZED);
        }
        userRepository.setUserVerifiedField(user.getId(), Boolean.TRUE);
        return new ResponseEntity<>("Email verified", HttpStatus.OK);
    }

    @RequestMapping(value = "/login")
    public ResponseEntity<TokenData> login(@RequestParam(value = "email") String email, @RequestParam(value = "password") String password){
        User user = userRepository.findByEmail(email);
        if(user == null){
            return new ResponseEntity<>(null, HttpStatus.NOT_ACCEPTABLE);
        }
        if(!user.getPassword().equals(password)){
            return new ResponseEntity<>(null, HttpStatus.NOT_ACCEPTABLE);
        }
        if(user.getVerified() == Boolean.FALSE){
            return  new ResponseEntity<>(null, HttpStatus.PRECONDITION_FAILED);
        }
        Date now = Date.from(Instant.now());
        String access = authService.createAccessToken(user.getId(), now);
        String refresh = authService.createRefreshToken(user.getId(), now);

        TokenData tokenData = new TokenData(access, refresh, authService.getAccessTokenExpireDate(now),
                authService.getRefreshTokenExpireDate(now));
        userRepository.updateTokens(user.getId(), access, refresh);
        return  new ResponseEntity<>(tokenData, HttpStatus.OK);
    }

    @RequestMapping(value = "/api/refresh", method = RequestMethod.GET)
    public ResponseEntity<TokenData> refresh(@RequestParam String refresh_token){
        if(authService.tokenHasErrors(refresh_token, false)){
            return  new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }
        Claims claims = authService.getClaims(refresh_token);
        Long id = Long.parseLong(claims.getSubject());
        User user = userRepository.getById(id);
        if(user == null){
            return new ResponseEntity<>(null, HttpStatus.NOT_ACCEPTABLE);
        }
        if(!user.getRefresh_token().equals(refresh_token)){
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }

        Date now = Date.from(Instant.now());
        String access = authService.createAccessToken(id, now);
        String refresh = authService.createRefreshToken(id, now);
        TokenData tokenData = new TokenData(access, refresh, authService.getAccessTokenExpireDate(now), authService.getRefreshTokenExpireDate(now));
        userRepository.updateTokens(id, access, refresh);
        return new ResponseEntity<>(tokenData, HttpStatus.OK);
    }




    @RequestMapping(value = "/clear")
    public ResponseEntity<String> clear(){

        userRepository.deleteAll();
        return new ResponseEntity<>("DELETED", HttpStatus.OK);
    }


}
