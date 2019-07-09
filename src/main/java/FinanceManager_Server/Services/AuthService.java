package FinanceManager_Server.Services;


import FinanceManager_Server.Database.BudgetRepository;
import FinanceManager_Server.Database.CategoryRepository;
import FinanceManager_Server.Database.Entity.User;
import FinanceManager_Server.Database.TransactionRepository;
import FinanceManager_Server.Database.UserRepository;
import FinanceManager_Server.PropertiesDataManager;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;
import java.util.List;

@Service
public class AuthService {

    PropertiesDataManager propertiesDataManager;
    UserRepository userRepository;
    TransactionRepository transactionRepository;
    CategoryRepository categoryRepository;
    BudgetRepository budgetRepository;

    public AuthService(PropertiesDataManager propertiesDataManager, UserRepository userRepository, TransactionRepository transactionRepository, CategoryRepository categoryRepository, BudgetRepository budgetRepository) {
        this.propertiesDataManager = propertiesDataManager;
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
        this.categoryRepository = categoryRepository;
        this.budgetRepository = budgetRepository;
    }

    public String createAccessToken(Long userId){
        String jwt = Jwts.builder().setIssuer("Finance Manager Server")
                .setSubject(userId.toString())
                .setIssuedAt(Date.from(Instant.now()))
                .setExpiration(Date.from(Instant.now().plusSeconds(60 * 30)))
                .claim("type", "access")
                .signWith(SignatureAlgorithm.HS256, propertiesDataManager.getEncodedSecretKey())
                .compact();
        return jwt;
    }

    public String createRefreshToken(Long userId){
        String jwt = Jwts.builder().setIssuer("Finance Manager Server")
                .setSubject(userId.toString())
                .setIssuedAt(Date.from(Instant.now()))
                .setExpiration(Date.from(Instant.now().plusSeconds(60 * 60 * 24 * 20)))
                .claim("type", "refresh")
                .signWith(SignatureAlgorithm.HS256, propertiesDataManager.getEncodedSecretKey())
                .compact();
        return jwt;
    }

    public boolean checkIsExpired(String token){
        Claims claims = getClaims(token);

        Date expireDate = claims.getExpiration();
        if(Date.from(Instant.now()).getTime() > expireDate.getTime()){
            return true;
        }
        else{
            return false;
        }
    }

    public Date getAccessTokenExpireDate(Date date){
        return  new Date(date.getTime() + 1000l * 60 * 30); //30 min
    }

    public Date getRefreshTokenExpireDate(Date date){
        return new Date(date.getTime() + 1000l * 60 * 60 * 24 * 20); //20 days
    }

    public Claims getClaims(String token){
        return Jwts.parser().setSigningKey(propertiesDataManager.getEncodedSecretKey())
                .parseClaimsJws(token).getBody();

    }

    public User getUserByToken(String access_token) throws InvalidTokenException, UserNotFoundException{
        if (checkIsExpired(access_token)){
            throw new TokenExpiredException();
        }
        Claims claims = getClaims(access_token);
        if (!claims.get("type", String.class).equals("access")){
            throw new InvalidTokenException("Unsupported token type");
        }
        User user = userRepository.getById(Long.parseLong(claims.getSubject()));
        if (user == null){
            throw new UserNotFoundException();
        }
        return user;
    }

    public boolean checkUserInDatabase(String email){
        User user = userRepository.findByEmail(email);
        return user == null;
    }


    public class TokenExpiredException extends RuntimeException{
        @Override
        public String getMessage() {
            return "Token expired";
        }
    }
    public class UserNotFoundException extends Exception{
        @Override
        public String getMessage() {
            return "User not found";
        }
    }
    public class InvalidTokenException extends Exception{
        public InvalidTokenException(String message) {
            super(message);
        }
    }
}
