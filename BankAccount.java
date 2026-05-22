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
    
    // 결제 기능 (비밀번호 검사, 결제 한도 검사 적용)
    public void payment(long amount, String inputPassword) {
    	// 비밀번호가 틀렸을 경우 오류 발생
    	if (!checkPassword(inputPassword)) {
    		throw new IllegalArgumentException("비밀번호가 틀렸습니다.");
    	}
    	// 1회 결제 한도(500만원)를 넘기면 오류 발생
    	if (amount > 5000000) {
    		throw new IllegalArgumentException("1회 결제 한도 500만원을 초과했습니다.");
    	}
		// 오류가 발생하지 않았다면 결제 진행
    	withdraw(amount);
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
