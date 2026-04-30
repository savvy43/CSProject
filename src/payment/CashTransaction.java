package payment;

public class CashTransaction extends BillTransaction {

    private double cash;

    public CashTransaction(double amount, double cash) {
        super(amount);
        this.cash = cash;
    }

    public boolean initiateTransaction() {
        if (cash >= amount) {
            setStatus(PaymentStatus.COMPLETED);
            return true;
        }
        setStatus(PaymentStatus.FAILED);
        return false;
    }

    public double getChange() {
        return cash - amount;
    }
}