public class Person {
    String name;
    Address address;
    String email;
    String phone;
    AccountType accountType;

    Person(String name, Address address, String email, String phone, AccountType accountType){

    }

}

class Account{
    private String id;
    private String password;
    private AccountStatus status;

    Account(String id, String password, AccountStatus status){
        this.id = id;
        this.password = password;
        this.status = status;
    }

    boolean resetPassword(String password){
        return true;
    }
}
