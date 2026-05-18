public class BankAccount {
    private String accountNumber;
    private String accountHolder;
    private long balance;
    
    // 새로 추가된 필드
    private String password;       // 계좌 비밀번호
    private long overdraftLimit;   // 마이너스 한도 (기본 0, 마이너스 통장일 경우 설정)

    // 생성자에 비밀번호와 마이너스 한도 추가
    public BankAccount(String accountNumber, String accountHolder, String password, long initialBalance, long overdraftLimit) {
        if (initialBalance < 0) {
            throw new IllegalArgumentException("초기 잔액은 0원 이상이어야 합니다.");
        }
        this.accountNumber = accountNumber;
        this.accountHolder = accountHolder;
        this.password = password;
        this.balance = initialBalance;
        this.overdraftLimit = overdraftLimit;
    }

    // 비밀번호 확인 
    public boolean checkPassword(String inputPassword) {
        return this.password.equals(inputPassword);
    }

    // 입금 기능
    public void deposit(long amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("입금액은 0원보다 커야 합니다.");
        }
        this.balance += amount;
    }

    // 출금 기능 (마이너스 한도 검사 적용)
    public void withdraw(long amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("출금액은 0원보다 커야 합니다.");
        }
        // 출금 요청 금액이 (현재 잔액 + 마이너스 한도)보다 크면 거절
        if (amount > this.balance + this.overdraftLimit) {
            throw new IllegalArgumentException("잔액 및 한도가 부족합니다.");
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

    // Getter 메서드들
    public String getAccountNumber() { return accountNumber; }
    public String getAccountHolder() { return accountHolder; }
    public long getBalance() { return balance; }
    public long getOverdraftLimit() { return overdraftLimit; }
}
