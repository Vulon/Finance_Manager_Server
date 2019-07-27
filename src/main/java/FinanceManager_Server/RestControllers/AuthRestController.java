package FinanceManager_Server.RestControllers;


import FinanceManager_Server.Database.Entity.Category;
import FinanceManager_Server.Database.Repositories.*;
import FinanceManager_Server.Database.Entity.User;
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
    TransactionActionRepository transactionActionRepository;
    CategoryActionRepository categoryActionRepository;
    BudgetActionRepository budgetActionRepository;
    AuthService authService;
    EmailService emailService;

    public static enum ServerResponseCode {
        OK,
        USER_NOT_FOUND,
        INVALID_TOKEN,
        EMAIL_NOT_VERIFIED,
        INTERNAL_SERVER_ERROR,
        CONNECTION_TIMEOUT,
        ALREADY_REGISTERED
    }


    public AuthRestController(UserRepository userRepository, TransactionRepository transactionRepository, CategoryRepository categoryRepository, BudgetRepository budgetRepository, TransactionActionRepository transactionActionRepository, CategoryActionRepository categoryActionRepository, BudgetActionRepository budgetActionRepository, AuthService authService, EmailService emailService) {
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
        this.categoryRepository = categoryRepository;
        this.budgetRepository = budgetRepository;
        this.transactionActionRepository = transactionActionRepository;
        this.categoryActionRepository = categoryActionRepository;
        this.budgetActionRepository = budgetActionRepository;
        this.authService = authService;
        this.emailService = emailService;
    }

    public static HttpStatus mapResponseCode(ServerResponseCode code){
        if(code == ServerResponseCode.OK){
            return HttpStatus.OK;
        }else if(code == ServerResponseCode.USER_NOT_FOUND){
            return HttpStatus.NOT_ACCEPTABLE;
        }else if(code == ServerResponseCode.INVALID_TOKEN){
            return HttpStatus.UNAUTHORIZED;
        }else if(code == ServerResponseCode.EMAIL_NOT_VERIFIED){
            return HttpStatus.PRECONDITION_FAILED;
        }else if(code == ServerResponseCode.INTERNAL_SERVER_ERROR){
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }else if(code == ServerResponseCode.ALREADY_REGISTERED){
            return HttpStatus.CONFLICT;
        }else{
            return HttpStatus.NOT_FOUND;
        }
    }


    @PostMapping(value = "/register",  produces = "application/json")
    public ResponseEntity<String> register(@RequestParam(value = "email") String email, @RequestParam(value = "password") String password,
                                   @RequestParam(value = "lang", required = false, defaultValue = "ENGLISH") String lang){
        User user = userRepository.findByEmail(email);
        if(user == null){
            user = new User(email, password, lang, Boolean.FALSE, "", "");
            System.out.println("Saving " + user);
            user = userRepository.saveAndFlush(user);
            create_base_categories(user.getEmail());
        }else if (user.getVerified() == Boolean.TRUE){
            return new ResponseEntity<>("ALREADY_REGISTERED", mapResponseCode(ServerResponseCode.ALREADY_REGISTERED));
        }
        Date now = Date.from(Instant.now());
        String access_token = authService.createAccessToken(user.getId(), now);
        String refresh_token = authService.createRefreshToken(user.getId(), now);
        userRepository.updateTokens(user.getId(), access_token, refresh_token);
        String url = emailService.sendVerification(email, access_token);
        return new ResponseEntity<>(url, mapResponseCode(ServerResponseCode.OK));
        //FOR DEBUG purpose send back access token
    }

    @RequestMapping(value = "/verify", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<String> verify(@RequestParam String access_token){
        System.out.println("Verify initiated");
        if(authService.tokenHasErrors(access_token, true)){
            return new ResponseEntity<>("Token incorrect", mapResponseCode(ServerResponseCode.INVALID_TOKEN));
        }

        User user = userRepository.getById(authService.getUserId(access_token));
        if(user == null){
            return new ResponseEntity<>("User not found", mapResponseCode(ServerResponseCode.USER_NOT_FOUND));
        }
        if(!user.getAccess_token().equals(access_token)){
            return new ResponseEntity<>("Token mismatch", mapResponseCode(ServerResponseCode.INVALID_TOKEN));
        }
        userRepository.setUserVerifiedField(user.getId(), Boolean.TRUE);

        return new ResponseEntity<>("Email verified", mapResponseCode(ServerResponseCode.OK));
    }

    @RequestMapping(value = "/login")
    public ResponseEntity<TokenData> login(@RequestParam(value = "email") String email, @RequestParam(value = "password") String password){
        User user = userRepository.findByEmail(email);
        if(user == null){
            return new ResponseEntity<>(null, mapResponseCode(ServerResponseCode.USER_NOT_FOUND));
        }
        if(!user.getPassword().equals(password)){
            return new ResponseEntity<>(null, mapResponseCode(ServerResponseCode.USER_NOT_FOUND));
        }
        if(user.getVerified() == Boolean.FALSE){
            return  new ResponseEntity<>(null, mapResponseCode(ServerResponseCode.EMAIL_NOT_VERIFIED));
        }
        Date now = Date.from(Instant.now());
        String access = authService.createAccessToken(user.getId(), now);
        String refresh = authService.createRefreshToken(user.getId(), now);

        TokenData tokenData = new TokenData(access, refresh, authService.getAccessTokenExpireDate(now),
                authService.getRefreshTokenExpireDate(now));
        userRepository.updateTokens(user.getId(), access, refresh);
        return  new ResponseEntity<>(tokenData, mapResponseCode(ServerResponseCode.OK));
    }

    @RequestMapping(value = "/api/refresh", method = RequestMethod.GET)
    public ResponseEntity<TokenData> refresh(@RequestParam String refresh_token){
        if(authService.tokenHasErrors(refresh_token, false)){
            return  new ResponseEntity<>(null, mapResponseCode(ServerResponseCode.INVALID_TOKEN));
        }
        User user = userRepository.getById(authService.getUserId(refresh_token));
        if(user == null){
            return new ResponseEntity<>(null, mapResponseCode(ServerResponseCode.USER_NOT_FOUND));
        }
        if(!user.getRefresh_token().equals(refresh_token)){
            return new ResponseEntity<>(null, mapResponseCode(ServerResponseCode.INVALID_TOKEN));
        }

        Date now = Date.from(Instant.now());
        String access = authService.createAccessToken(user.getId(), now);
        String refresh = authService.createRefreshToken(user.getId(), now);
        TokenData tokenData = new TokenData(access, refresh, authService.getAccessTokenExpireDate(now), authService.getRefreshTokenExpireDate(now));
        userRepository.updateTokens(user.getId(), access, refresh);
        return new ResponseEntity<>(tokenData, mapResponseCode(ServerResponseCode.OK));
    }




    @RequestMapping(value = "/clear")
    @ResponseBody
    public String clear(){
        userRepository.deleteAll();
        transactionRepository.deleteAll();
        budgetRepository.deleteAll();
        categoryRepository.deleteAll();
        transactionActionRepository.deleteAll();
        categoryActionRepository.deleteAll();
        budgetActionRepository.deleteAll();
        return "DELETED";
    }

    private void create_base_categories(String user_email){
        User user = userRepository.findByEmail(user_email);
        Category income = new Category(user.getId(), "#1fed45", "Income", 1, null, Date.from(Instant.now()));
        Category expense = new Category(user.getId(), "#4e7dea", "Expense", 0, null, Date.from(Instant.now()));
        income.setCategory(1l);
        expense.setCategory(0l);
        System.out.println("CREATE_BASE_CATEGORIES ");
        System.out.println("Created " + income);
        System.out.println("Created " + expense);
        categoryRepository.saveAndFlush(income);
        categoryRepository.saveAndFlush(expense);
        System.out.println("SAVED CATEGORIES TO REPOSITORY");
    }


}
