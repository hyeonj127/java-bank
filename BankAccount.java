public class BankAccount {
    private String accountNumber;
    private String accountHolder;
    private long balance;

    public BankAccount(String accountNumber, String accountHolder, long initialBalance) {
        if (initialBalance < 0) {
            throw new IllegalArgumentException("초기 잔액은 0원 이상이어야 합니다.");
        }
        this.accountNumber = accountNumber;
        this.accountHolder = accountHolder;
        this.balance = initialBalance;
    }

    // 입금 기능
    public void deposit(long amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("입금액은 0원보다 커야 합니다.");
        }
        this.balance += amount;
    }

    // 출금 기능
    public void withdraw(long amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("출금액은 0원보다 커야 합니다.");
        }
        if (amount > this.balance) {
            throw new IllegalArgumentException("잔액이 부족합니다.");
        }
        this.balance -= amount;
    }

    // 계좌 이체 기능
    public void transfer(BankAccount targetAccount, long amount) {
        if (targetAccount == null) {
            throw new IllegalArgumentException("이체 대상 계좌가 존재하지 않습니다.");
        }
        this.withdraw(amount);
        targetAccount.deposit(amount); 
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getAccountHolder() {
        return accountHolder;
    }

    public long getBalance() {
        return balance;
    }
}
