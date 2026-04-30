package payment;

public class CheckTransaction extends BillTransaction {

    private String bank;
    private String checkNumber;

    public CheckTransaction(double amount, String bank, String checkNumber) {
        super(amount);
        this.bank = bank;
        this.checkNumber = checkNumber;
    }

    public boolean initiateTransaction() {
        if (bank != null && checkNumber != null) {
            setStatus(PaymentStatus.COMPLETED);
            return true;
        }
        setStatus(PaymentStatus.FAILED);
        return false;
    }
}