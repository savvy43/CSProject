package payment;

public class CreditCardTransaction extends BillTransaction {

    private String name;
    private String zip;

    public CreditCardTransaction(double amount, String name, String zip) {
        super(amount);
        this.name = name;
        this.zip = zip;
    }

    public boolean initiateTransaction() {
        if (name != null && zip != null) {
            setStatus(PaymentStatus.COMPLETED);
            return true;
        }
        setStatus(PaymentStatus.FAILED);
        return false;
    }
}