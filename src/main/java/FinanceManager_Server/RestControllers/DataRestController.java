package FinanceManager_Server.RestControllers;


import FinanceManager_Server.Database.Entity.User;
import FinanceManager_Server.Database.Repositories.*;
import FinanceManager_Server.Services.AuthService;
import FinanceManager_Server.TransportableDataObjects.ActionQueue;
import io.jsonwebtoken.Claims;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
public class DataRestController {
    UserRepository userRepository;
    TransactionRepository transactionRepository;
    CategoryRepository categoryRepository;
    BudgetRepository budgetRepository;
    AuthService authService;
    TransactionActionRepository transactionActionRepository;
    CategoryActionRepository categoryActionRepository;
    BudgetActionRepository budgetActionRepository;


    public DataRestController(UserRepository userRepository, TransactionRepository transactionRepository, CategoryRepository categoryRepository, BudgetRepository budgetRepository, AuthService authService, TransactionActionRepository transactionActionRepository, CategoryActionRepository categoryActionRepository, BudgetActionRepository budgetActionRepository) {
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
        this.categoryRepository = categoryRepository;
        this.budgetRepository = budgetRepository;
        this.authService = authService;
        this.transactionActionRepository = transactionActionRepository;
        this.categoryActionRepository = categoryActionRepository;
        this.budgetActionRepository = budgetActionRepository;
    }

    @GetMapping(name = "/api/get_updates")
    public ResponseEntity<ActionQueue> getUpdates(String access_token, Date last_update){
        if(authService.tokenHasErrors(access_token, true)){
            return  new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }
        Claims claims = authService.getClaims(access_token);
        User user = userRepository.getById(Long.parseLong(claims.getSubject()));
        if(user == null){
            return new ResponseEntity<>(null, HttpStatus.NOT_ACCEPTABLE);
        }


    }
}
