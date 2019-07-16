package FinanceManager_Server.RestControllers;


import FinanceManager_Server.Database.Entity.*;
import FinanceManager_Server.Database.Repositories.*;
import FinanceManager_Server.Services.AuthService;
import FinanceManager_Server.TransportableDataObjects.ActionQueue;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

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

    public static final int MAX_DELETE_STACK_SIZE = 30;


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
            return  new ResponseEntity<>(null, AuthRestController.mapResponseCode(AuthRestController.ServerResponseCode.INVALID_TOKEN));
        }

        User user = userRepository.getById(authService.getUserId(access_token));
        if(user == null){
            return new ResponseEntity<>(null, AuthRestController.mapResponseCode(AuthRestController.ServerResponseCode.USER_NOT_FOUND));
        }
        if(!user.getAccess_token().equals(access_token)){
            return  new ResponseEntity<>(null, AuthRestController.mapResponseCode(AuthRestController.ServerResponseCode.INVALID_TOKEN));
        }
        ArrayList<CategoryAction> categoryActions = CategoryAction.toCategoryAction(categoryRepository.getAllByCommitDateAfter(last_update));
        {
            ArrayList<CategoryAction> temp = (ArrayList<CategoryAction>)categoryActionRepository.getAllByCommitDateAfter(last_update);
            if(temp != null)
                categoryActions.addAll(temp);
        }
        ArrayList<TransactionAction> transactionActions = TransactionAction.toTransactionAction(transactionRepository.getAllByCommitDateAfter(last_update));
        {
            ArrayList<TransactionAction> temp = (ArrayList<TransactionAction>)transactionActionRepository.getAllByCommitDateAfter(last_update);
            if(temp != null){
                transactionActions.addAll(temp);
            }
        }
        ArrayList<BudgetAction> budgetActions = BudgetAction.toBudgetAction(budgetRepository.getAllByCommitDateAfter(last_update));
        {
            ArrayList<BudgetAction> temp = (ArrayList<BudgetAction>)budgetActionRepository.getAllByCommitDateAfter(last_update);
            if(temp != null){
                budgetActions.addAll(temp);
            }
        }
        Collections.sort(categoryActions);
        Collections.sort(transactionActions);
        Collections.sort(budgetActions);
        ActionQueue actionQueue = new ActionQueue(budgetActions, transactionActions, categoryActions);
        return new ResponseEntity<>(actionQueue, AuthRestController.mapResponseCode(AuthRestController.ServerResponseCode.OK));
    }

    @PostMapping(name = "/api/post_updates")
    public ResponseEntity<ActionQueue> postUpdates(String access_token, ActionQueue actionQueue){
        Queue<Action> actions = actionQueue.getActionsQueue();
        if(authService.tokenHasErrors(access_token, true)){
            return new ResponseEntity<>(null, AuthRestController.mapResponseCode(AuthRestController.ServerResponseCode.INVALID_TOKEN));
        }
        User user = userRepository.getById(authService.getUserId(access_token));
        if(user == null){
            return new ResponseEntity<>(null, AuthRestController.mapResponseCode(AuthRestController.ServerResponseCode.USER_NOT_FOUND));
        }
        if(!user.getAccess_token().equals(access_token)){
            return new ResponseEntity<>(null, AuthRestController.mapResponseCode(AuthRestController.ServerResponseCode.INVALID_TOKEN));
        }
        ActionQueue completedActions = processActionQueue(user.getId(), actions);
        if (actions.size() > completedActions.getActionsQueue().size()){ //try again for failed actions
            Queue<Action> completed = completedActions.getActionsQueue();
            LinkedList<Action> failedActions = new LinkedList<>();
            while(actions.size() > 0){
                Action temp = actions.remove();
                if(!completed.contains(temp)){
                    failedActions.add(temp);
                }
            }
            completedActions.addAll(processActionQueue(user.getId(), failedActions));
        }

        return new ResponseEntity<>(completedActions, AuthRestController.mapResponseCode(AuthRestController.ServerResponseCode.OK));
    }

    private Long generateFreeId(Long user_id, String repositoryClassName){
        if(repositoryClassName.equals(budgetRepository.getClass().getName())){
            return budgetRepository.getMaxId(user_id) + 1;
        }else if(repositoryClassName.equals(categoryRepository.getClass().getName())){
            return categoryRepository.getMaxId(user_id) + 1;
        }else if(repositoryClassName.equals(transactionRepository.getClass().getName())){
            return transactionRepository.getMaxId(user_id) + 1;
        }else{
            return -1l;
        }

    }

    private ActionQueue processActionQueue(Long user_id, Queue<Action> actions){
        ActionQueue completedActions = new ActionQueue();
        while (actions.size() > 0){
            Action action = actions.remove();
            switch (action.getType()){
                case "transaction":{
                    TransactionAction transactionAction = (TransactionAction) action;
                    try{
                        if(transactionAction.isCreate()){
                            Category category = categoryRepository.getByUserAndCategory(user_id, transactionAction.getCategory_id());
                            if(transactionRepository.getByUserAndTransaction(user_id, transactionAction.getTransaction()) != null){
                                Long freeId = generateFreeId(user_id, transactionRepository.getClass().getName());
                                transactionAction.setTransaction(freeId);
                            }
                            transactionRepository.saveAndFlush(new Transaction(transactionAction, category));
                        }else{
                            transactionRepository.deleteByUserAndTransaction(user_id, transactionAction.getTransaction());
                            transactionRepository.flush();
                            int size = transactionActionRepository.countByUserAndTransaction(user_id, transactionAction.getTransaction());
                            if(size >= MAX_DELETE_STACK_SIZE){
                                transactionActionRepository.deleteOldest(user_id);
                                transactionActionRepository.flush();
                            }
                            transactionActionRepository.saveAndFlush(transactionAction);
                        }
                        completedActions.addTransactionAction(transactionAction);
                    }catch (Exception e){
                        System.err.println(e.getMessage());
                    }
                    break;
                }
                case "budget":{
                    BudgetAction budgetAction = (BudgetAction) action;
                    try{
                        if(budgetAction.isCreate()){
                            if(budgetRepository.findByUserAndBudget(user_id, budgetAction.getBudget()) != null){
                                Long freeId = generateFreeId(user_id, budgetRepository.getClass().getName());
                                budgetAction.setBudget(freeId);
                            }
                            Set<Category> categoriesOld = budgetAction.getCategories();
                            Budget budget = new Budget(budgetAction);
                            budget.setCategories(new HashSet<>());
                            for(Category c : categoriesOld){
                                budget.addCategory(categoryRepository.getByUserAndCategory(user_id, c.getCategory()));
                            }
                            budgetRepository.saveAndFlush(budget);
                        }else{
                            budgetRepository.deleteByUserAndBudget(user_id, budgetAction.getBudget());
                            budgetRepository.flush();
                            int size = budgetActionRepository.countByUserAndBudget(user_id, budgetAction.getBudget());
                            if(size >= MAX_DELETE_STACK_SIZE){
                                budgetActionRepository.deleteOldest(user_id);
                                budgetActionRepository.flush();
                            }
                            budgetAction.setCategories(new HashSet<>());
                            budgetActionRepository.saveAndFlush(budgetAction);
                        }
                        completedActions.addBudgetAction(budgetAction);
                    }catch (Exception e){
                        System.err.println(e.getMessage());
                    }
                    break;
                }
                case "category":{
                    CategoryAction categoryAction = (CategoryAction) action;
                    if(categoryAction.isCreate()){
                        if(categoryRepository.getByUserAndCategory(user_id, categoryAction.getCategory()) != null){
                            Long freeId = generateFreeId(user_id, categoryRepository.getClass().getName());
                            categoryAction.setCategory(freeId);
                        }
                        Category parent = categoryRepository.getByUserAndCategory(user_id, categoryAction.getParent_id());
                        categoryRepository.saveAndFlush(new Category(categoryAction, parent));
                    }else{
                        Category parent = categoryRepository.getByUserAndCategory(user_id, categoryAction.getParent_id());
                        Category thisCategory = categoryRepository.getByUserAndCategory(categoryAction.getUser(), categoryAction.getCategory());
                        List<Budget> budgetList = budgetRepository.getAllByUserAndCategory(user_id, thisCategory);
                        for(Budget b : budgetList){ //Clear category reference in budget tables
                            try{
                                budgetRepository.deleteByUserAndBudget(b.getUser(), b.getBudget());
                                budgetRepository.flush();
                                Set<Category> temp = b.getCategories();
                                temp.remove(thisCategory);
                                temp.add(parent);
                                b.setCategories(temp);
                                budgetRepository.saveAndFlush(b);
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                        List<Transaction> transactionList = transactionRepository.getAllByCategory(thisCategory);
                        for(Transaction t : transactionList){ //Clear category reference in transaction table
                            try{
                                transactionRepository.deleteByUserAndTransaction(user_id, t.getTransaction());
                                transactionRepository.flush();
                                t.setCategory(parent);
                                transactionRepository.saveAndFlush(t);
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                        int size = categoryActionRepository.countByUserAndCategory(user_id, categoryAction.getCategory());
                        if(size > MAX_DELETE_STACK_SIZE){
                            categoryActionRepository.deleteOldest(user_id);
                            categoryActionRepository.flush();
                        }
                        categoryActionRepository.saveAndFlush(categoryAction);
                    }
                    completedActions.addCategoryAction(categoryAction);
                }
            }
        }
        return completedActions;
    }
}
