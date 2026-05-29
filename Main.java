package team;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);
    private static final BankService bankService = new BankService();

    public static void main(String[] args) {

        // 테스트용 기본 계좌
        bankService.createAccount("1111", "김철수", "1234", 100000, 0);
        bankService.createAccount("2222", "이영희", "5678", 50000, 100000);

        while (true) {
            printMenu();

            int choice = inputInt("메뉴 선택: ");

            try {
                switch (choice) {
                    case 1:
                        createAccountMenu();
                        break;
                    case 2:
                        depositMenu();
                        break;
                    case 3:
                        withdrawMenu();
                        break;
                    case 4:
                        paymentMenu();
                        break;
                    case 5:
                        transferMenu();
                        break;
                    case 6:
                        showAccountInfoMenu();
                        break;
                    case 7:
                        bankService.showAllAccounts();
                        break;
                    case 8:
                        showTransactionHistoryMenu();
                        break;
                    case 0:
                        System.out.println("프로그램을 종료합니다.");
                        return;
                    default:
                        System.out.println("올바른 메뉴 번호를 입력하세요.");
                }
            } catch (IllegalArgumentException e) {
                System.out.println("오류: " + e.getMessage());
            }
        }
    }

    private static void printMenu() {
        System.out.println("\n========== 은행 계좌 관리 프로그램 ==========");
        System.out.println("1. 계좌 생성");
        System.out.println("2. 입금");
        System.out.println("3. 출금");
        System.out.println("4. 결제");
        System.out.println("5. 계좌 이체");
        System.out.println("6. 계좌 정보 조회");
        System.out.println("7. 전체 계좌 조회");
        System.out.println("8. 거래내역 조회");
        System.out.println("0. 종료");
        System.out.println("========================================");
    }

    private static void createAccountMenu() {
        String accountNumber = inputString("계좌번호: ");
        String accountHolder = inputString("예금주: ");
        String password = inputString("비밀번호: ");
        long initialBalance = inputLong("초기 잔액: ");
        long overdraftLimit = inputLong("마이너스 한도: ");

        bankService.createAccount(accountNumber, accountHolder, password, initialBalance, overdraftLimit);
    }

    private static void depositMenu() {
        String accountNumber = inputString("입금할 계좌번호: ");
        long amount = inputLong("입금액: ");

        bankService.deposit(accountNumber, amount);
    }

    private static void withdrawMenu() {
        String accountNumber = inputString("출금할 계좌번호: ");
        String password = inputString("비밀번호: ");
        long amount = inputLong("출금액: ");

        bankService.withdraw(accountNumber, password, amount);
    }

    private static void paymentMenu() {
        String accountNumber = inputString("결제할 계좌번호: ");
        String password = inputString("비밀번호: ");
        long amount = inputLong("결제액: ");

        bankService.payment(accountNumber, password, amount);
    }

    private static void transferMenu() {
        String senderAccountNumber = inputString("보내는 계좌번호: ");
        String password = inputString("비밀번호: ");
        String targetAccountNumber = inputString("받는 계좌번호: ");
        long amount = inputLong("이체액: ");

        bankService.transfer(senderAccountNumber, password, targetAccountNumber, amount);
    }

    private static void showAccountInfoMenu() {
        String accountNumber = inputString("조회할 계좌번호: ");

        bankService.showAccountInfo(accountNumber);
    }

    private static void showTransactionHistoryMenu() {
        String accountNumber = inputString("거래내역을 조회할 계좌번호: ");
        String password = inputString("비밀번호: ");

        bankService.showTransactionHistory(accountNumber, password);
    }

    private static String inputString(String message) {
        System.out.print(message);
        return scanner.nextLine().trim();
    }

    private static int inputInt(String message) {
        while (true) {
            try {
                System.out.print(message);
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("숫자를 입력하세요.");
            }
        }
    }

    private static long inputLong(String message) {
        while (true) {
            try {
                System.out.print(message);
                return Long.parseLong(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("올바른 금액을 숫자로 입력하세요.");
            }
        }
    }
}

enum TransactionType {
    DEPOSIT,
    WITHDRAW,
    PAYMENT,
    TRANSFER_IN,
    TRANSFER_OUT
}

class Transaction {

    private TransactionType type;
    private long amount;
    private LocalDateTime transactionTime;
    private long balanceAfterTransaction;
    private String targetAccountNumber;

    public Transaction(TransactionType type,
                       long amount,
                       long balanceAfterTransaction,
                       String targetAccountNumber) {

        this.type = type;
        this.amount = amount;
        this.balanceAfterTransaction = balanceAfterTransaction;
        this.targetAccountNumber = targetAccountNumber;
        this.transactionTime = LocalDateTime.now();
    }

    public TransactionType getType() {
        return type;
    }

    public long getAmount() {
        return amount;
    }

    public LocalDateTime getTransactionTime() {
        return transactionTime;
    }

    public long getBalanceAfterTransaction() {
        return balanceAfterTransaction;
    }

    public String getTargetAccountNumber() {
        return targetAccountNumber;
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        return "거래종류 : " + type +
                "\n거래금액 : " + amount + "원" +
                "\n거래시간 : " + transactionTime.format(formatter) +
                "\n거래후잔액 : " + balanceAfterTransaction + "원" +
                "\n상대계좌 : " + targetAccountNumber +
                "\n-------------------------";
    }
}

class BankAccount {

    private String accountNumber;
    private String accountHolder;
    private long balance;
    private String password;
    private long overdraftLimit;
    private ArrayList<Transaction> transactions;

    public BankAccount(String accountNumber,
                       String accountHolder,
                       String password,
                       long initialBalance,
                       long overdraftLimit) {

        if (accountNumber == null || accountNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("계좌번호는 비어 있을 수 없습니다.");
        }

        if (accountHolder == null || accountHolder.trim().isEmpty()) {
            throw new IllegalArgumentException("예금주는 비어 있을 수 없습니다.");
        }

        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("비밀번호는 비어 있을 수 없습니다.");
        }

        if (initialBalance < 0) {
            throw new IllegalArgumentException("초기 잔액은 0원 이상이어야 합니다.");
        }

        if (overdraftLimit < 0) {
            throw new IllegalArgumentException("마이너스 한도는 0원 이상이어야 합니다.");
        }

        this.accountNumber = accountNumber;
        this.accountHolder = accountHolder;
        this.password = password;
        this.balance = initialBalance;
        this.overdraftLimit = overdraftLimit;
        this.transactions = new ArrayList<>();
    }

    public boolean checkPassword(String inputPassword) {
        return this.password.equals(inputPassword);
    }

    private void validateAmount(long amount, String actionName) {
        if (amount <= 0) {
            throw new IllegalArgumentException(actionName + " 금액은 0원보다 커야 합니다.");
        }
    }

    private void depositInternal(long amount) {
        validateAmount(amount, "입금");
        this.balance += amount;
    }

    private void withdrawInternal(long amount) {
        validateAmount(amount, "출금");

        if (amount > this.balance + this.overdraftLimit) {
            throw new IllegalArgumentException("잔액 및 마이너스 한도가 부족합니다.");
        }

        this.balance -= amount;
    }

    public void deposit(long amount) {
        depositInternal(amount);

        addTransaction(new Transaction(
                TransactionType.DEPOSIT,
                amount,
                balance,
                "없음"
        ));
    }

    public void withdraw(long amount) {
        withdrawInternal(amount);

        addTransaction(new Transaction(
                TransactionType.WITHDRAW,
                amount,
                balance,
                "없음"
        ));
    }

    public void payment(long amount, String inputPassword) {

        if (!checkPassword(inputPassword)) {
            throw new IllegalArgumentException("비밀번호가 틀렸습니다.");
        }

        if (amount > 5000000) {
            throw new IllegalArgumentException("1회 결제 한도 500만원을 초과했습니다.");
        }

        withdrawInternal(amount);

        addTransaction(new Transaction(
                TransactionType.PAYMENT,
                amount,
                balance,
                "결제"
        ));
    }

    public void transfer(BankAccount targetAccount, long amount) {

        if (targetAccount == null) {
            throw new IllegalArgumentException("보낼 계좌 번호가 올바르지 않습니다.");
        }

        if (this == targetAccount) {
            throw new IllegalArgumentException("나 자신에게는 이체할 수 없습니다.");
        }

        if (amount <= 0) {
            throw new IllegalArgumentException("이체할 금액은 0원보다 많아야 합니다.");
        }

        this.withdrawInternal(amount);
        targetAccount.depositInternal(amount);

        this.addTransaction(new Transaction(
                TransactionType.TRANSFER_OUT,
                amount,
                this.balance,
                targetAccount.getAccountNumber()
        ));

        targetAccount.addTransaction(new Transaction(
                TransactionType.TRANSFER_IN,
                amount,
                targetAccount.getBalance(),
                this.accountNumber
        ));

        System.out.println("이체가 완료되었습니다.");
    }

    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);
    }

    public void showTransactionHistory() {
        if (transactions.isEmpty()) {
            System.out.println("거래내역이 없습니다.");
            return;
        }

        for (Transaction transaction : transactions) {
            System.out.println(transaction);
        }
    }

    public List<Transaction> getTransactions() {
        return new ArrayList<>(transactions);
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

    public long getOverdraftLimit() {
        return overdraftLimit;
    }
}

class BankService {

    private ArrayList<BankAccount> accounts;

    public BankService() {
        accounts = new ArrayList<>();
    }

    public void createAccount(String accountNumber,
                              String accountHolder,
                              String password,
                              long initialBalance,
                              long overdraftLimit) {

        if (findAccount(accountNumber) != null) {
            throw new IllegalArgumentException("이미 존재하는 계좌번호입니다.");
        }

        BankAccount account =
                new BankAccount(
                        accountNumber,
                        accountHolder,
                        password,
                        initialBalance,
                        overdraftLimit
                );

        accounts.add(account);
        System.out.println("계좌가 생성되었습니다.");
    }

    public BankAccount findAccount(String accountNumber) {
        for (BankAccount account : accounts) {
            if (account.getAccountNumber().equals(accountNumber)) {
                return account;
            }
        }
        return null;
    }

    private BankAccount getAccountOrThrow(String accountNumber) {
        BankAccount account = findAccount(accountNumber);

        if (account == null) {
            throw new IllegalArgumentException("해당 계좌를 찾을 수 없습니다.");
        }

        return account;
    }

    public void deposit(String accountNumber, long amount) {
        BankAccount account = getAccountOrThrow(accountNumber);
        account.deposit(amount);
        System.out.println("입금이 완료되었습니다.");
    }

    public void withdraw(String accountNumber, String password, long amount) {
        BankAccount account = getAccountOrThrow(accountNumber);

        if (!account.checkPassword(password)) {
            throw new IllegalArgumentException("비밀번호가 틀렸습니다.");
        }

        account.withdraw(amount);
        System.out.println("출금이 완료되었습니다.");
    }

    public void payment(String accountNumber, String password, long amount) {
        BankAccount account = getAccountOrThrow(accountNumber);
        account.payment(amount, password);
        System.out.println("결제가 완료되었습니다.");
    }

    public void transfer(String senderAccountNumber,
                         String password,
                         String targetAccountNumber,
                         long amount) {

        BankAccount sender = getAccountOrThrow(senderAccountNumber);
        BankAccount target = getAccountOrThrow(targetAccountNumber);

        if (!sender.checkPassword(password)) {
            throw new IllegalArgumentException("비밀번호가 틀렸습니다.");
        }

        sender.transfer(target, amount);
    }

    public void showAccountInfo(String accountNumber) {
        BankAccount account = getAccountOrThrow(accountNumber);

        System.out.println("계좌번호 : " + account.getAccountNumber());
        System.out.println("예금주 : " + account.getAccountHolder());
        System.out.println("잔액 : " + account.getBalance() + "원");
        System.out.println("마이너스 한도 : " + account.getOverdraftLimit() + "원");
    }

    public void showAllAccounts() {
        if (accounts.isEmpty()) {
            System.out.println("등록된 계좌가 없습니다.");
            return;
        }

        for (BankAccount account : accounts) {
            System.out.println("계좌번호 : " + account.getAccountNumber());
            System.out.println("예금주 : " + account.getAccountHolder());
            System.out.println("잔액 : " + account.getBalance() + "원");
            System.out.println("-------------------------");
        }
    }

    public void showTransactionHistory(String accountNumber, String password) {
        BankAccount account = getAccountOrThrow(accountNumber);

        if (!account.checkPassword(password)) {
            throw new IllegalArgumentException("비밀번호가 틀렸습니다.");
        }

        account.showTransactionHistory();
    }
    
 // GUI
    public ArrayList<BankAccount> getAllAccounts() {
        return this.accounts;
    }
}