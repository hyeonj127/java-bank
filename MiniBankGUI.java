package team; // 패키지명이 다르면 맞춰서 수정하세요

import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

public class MiniBankGUI extends JFrame {

    // 백엔드 로직 객체 연결
    private BankService bankService = new BankService();

    private final Font MAIN_FONT = new Font("맑은 고딕", Font.PLAIN, 14);
    private final Font TITLE_FONT = new Font("맑은 고딕", Font.BOLD, 16);

    // 테이블 모델 (전역 변수)
    private DefaultTableModel adminTableModel;
    private DefaultTableModel historyTableModel;

    public MiniBankGUI() {
        setTitle("Mini Bank Manager");
        setSize(850, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        UIManager.put("Label.font", MAIN_FONT);
        UIManager.put("RadioButton.font", MAIN_FONT);
        UIManager.put("Button.font", MAIN_FONT);

        // 테스트용 기본 계좌 세팅
        bankService.createAccount("1111", "김철수", "1234", 100000, 0);
        bankService.createAccount("2222", "이영희", "5678", 50000, 100000);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(TITLE_FONT);

        tabbedPane.addTab("고객 관리", createAdminTab());
        tabbedPane.addTab("개인 뱅킹", createBankingTab());
        tabbedPane.addTab("거래 내역", createHistoryTab());

        JPanel mainContainer = new JPanel(new BorderLayout());
        mainContainer.setBorder(new EmptyBorder(10, 10, 10, 10));
        mainContainer.add(tabbedPane, BorderLayout.CENTER);

        add(mainContainer);
        
        // 프로그램 시작 시 전체 계좌 목록 갱신
        refreshAdminTable(); 
    }

    // ====================================================
    // [보안] 더블클릭 수정 방지용 커스텀 테이블 모델
    // ====================================================
    private DefaultTableModel createNonEditableModel(String[] colNames) {
        return new DefaultTableModel(colNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // 무조건 수정 불가!
            }
        };
    }

    // ====================================================
    // [탭 1] 고객 관리 (계좌 개설)
    // ====================================================
    private JPanel createAdminTab() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // 왼쪽: 계좌 개설 폼
        JPanel formPanel = new JPanel(new BorderLayout(0, 10));
        formPanel.setPreferredSize(new Dimension(250, 0));
        formPanel.add(new JLabel("신규 계좌 개설"), BorderLayout.NORTH);

        JPanel inputPanel = new JPanel(new GridLayout(10, 1, 0, 5));
        JTextField txtAccNo = new JTextField();
        JTextField txtName = new JTextField();
        JPasswordField txtPw = new JPasswordField();
        JTextField txtInitBal = new JTextField();
        JTextField txtLimit = new JTextField();

        inputPanel.add(new JLabel("계좌번호:")); inputPanel.add(txtAccNo);
        inputPanel.add(new JLabel("고객명(예금주):")); inputPanel.add(txtName);
        inputPanel.add(new JLabel("비밀번호:")); inputPanel.add(txtPw);
        inputPanel.add(new JLabel("초기 잔액:")); inputPanel.add(txtInitBal);
        inputPanel.add(new JLabel("마이너스 한도:")); inputPanel.add(txtLimit);

        JButton btnCreate = new JButton("계좌 개설하기");
        formPanel.add(inputPanel, BorderLayout.CENTER);
        formPanel.add(btnCreate, BorderLayout.SOUTH);

        // 계좌 개설 이벤트
        btnCreate.addActionListener(e -> {
            try {
                String accNo = txtAccNo.getText();
                String name = txtName.getText();
                String pw = new String(txtPw.getPassword());
                long initBal = Long.parseLong(txtInitBal.getText());
                long limit = Long.parseLong(txtLimit.getText());

                bankService.createAccount(accNo, name, pw, initBal, limit);
                JOptionPane.showMessageDialog(this, "계좌가 성공적으로 생성되었습니다.");
                refreshAdminTable(); 
                
                txtAccNo.setText(""); txtName.setText(""); txtPw.setText("");
                txtInitBal.setText(""); txtLimit.setText("");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "금액과 한도는 숫자로만 입력해주세요.", "입력 오류", JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
            }
        });

        // 중앙: 전체 고객 표
        JPanel tablePanel = new JPanel(new BorderLayout(0, 5));
        tablePanel.add(new JLabel("전체 계좌 목록"), BorderLayout.NORTH);

        String[] colNames = {"계좌번호", "고객명", "잔액", "마이너스 한도"};
        adminTableModel = createNonEditableModel(colNames); // 🌟 수정 방지 모델 적용
        JTable table = createReadableTable(adminTableModel);
        tablePanel.add(new JScrollPane(table), BorderLayout.CENTER);

        panel.add(formPanel, BorderLayout.WEST);
        panel.add(tablePanel, BorderLayout.CENTER);

        return panel;
    }

    // ====================================================
    // [탭 2] 개인 뱅킹 (금융 거래)
    // ====================================================
    private JPanel createBankingTab() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JPanel actionPanel = new JPanel(new GridBagLayout());
        actionPanel.setBorder(BorderFactory.createTitledBorder("금융 거래 실행"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL; 
        gbc.insets = new Insets(10, 10, 10, 10);

        JRadioButton rdoDeposit = new JRadioButton("입금", true);
        JRadioButton rdoWithdraw = new JRadioButton("출금"); // 🌟 출금 추가
        JRadioButton rdoPayment = new JRadioButton("결제");
        JRadioButton rdoTransfer = new JRadioButton("계좌이체");
        
        ButtonGroup group = new ButtonGroup();
        group.add(rdoDeposit); group.add(rdoWithdraw); 
        group.add(rdoPayment); group.add(rdoTransfer);

        JPanel radioBox = new JPanel();
        radioBox.add(rdoDeposit); radioBox.add(rdoWithdraw); 
        radioBox.add(rdoPayment); radioBox.add(rdoTransfer);

        JTextField txtMyAcc = new JTextField(15);
        JPasswordField txtPw = new JPasswordField(15);
        JTextField txtAmount = new JTextField(15);
        
        JLabel lblTargetAcc = new JLabel("상대 계좌번호:");
        JTextField txtTargetAcc = new JTextField(15);
        JButton btnExecute = new JButton("거래 실행");

        // 🌟 이체할 때만 대상 계좌 입력칸 보이기
        lblTargetAcc.setVisible(false);
        txtTargetAcc.setVisible(false);

        rdoDeposit.addActionListener(e -> { lblTargetAcc.setVisible(false); txtTargetAcc.setVisible(false); });
        rdoWithdraw.addActionListener(e -> { lblTargetAcc.setVisible(false); txtTargetAcc.setVisible(false); });
        rdoPayment.addActionListener(e -> { lblTargetAcc.setVisible(false); txtTargetAcc.setVisible(false); });
        rdoTransfer.addActionListener(e -> { lblTargetAcc.setVisible(true); txtTargetAcc.setVisible(true); });

        int y = 0;
        gbc.gridx = 0; gbc.gridy = y++; gbc.gridwidth = 2; actionPanel.add(radioBox, gbc);
        gbc.gridwidth = 1;
        gbc.gridy = y; actionPanel.add(new JLabel("내 계좌번호:"), gbc); gbc.gridx = 1; actionPanel.add(txtMyAcc, gbc);
        gbc.gridx = 0; gbc.gridy = ++y; actionPanel.add(new JLabel("비밀번호:"), gbc); gbc.gridx = 1; actionPanel.add(txtPw, gbc);
        gbc.gridx = 0; gbc.gridy = ++y; actionPanel.add(new JLabel("거래 금액:"), gbc); gbc.gridx = 1; actionPanel.add(txtAmount, gbc);
        
        gbc.gridx = 0; gbc.gridy = ++y; actionPanel.add(lblTargetAcc, gbc); 
        gbc.gridx = 1; actionPanel.add(txtTargetAcc, gbc);
        
        gbc.gridx = 0; gbc.gridy = ++y; gbc.gridwidth = 2; gbc.insets = new Insets(20, 10, 10, 10);
        actionPanel.add(btnExecute, gbc);

        // 거래 실행 이벤트
        btnExecute.addActionListener(e -> {
            try {
                String myAcc = txtMyAcc.getText();
                long amount = Long.parseLong(txtAmount.getText());
                String pw = new String(txtPw.getPassword());

                if (rdoDeposit.isSelected()) {
                    bankService.deposit(myAcc, amount);
                    JOptionPane.showMessageDialog(this, amount + "원 입금 완료!");
                } else if (rdoWithdraw.isSelected()) { // 🌟 출금 로직 연결
                    bankService.withdraw(myAcc, pw, amount);
                    JOptionPane.showMessageDialog(this, amount + "원 출금 완료!");
                } else if (rdoPayment.isSelected()) {
                    bankService.payment(myAcc, pw, amount);
                    JOptionPane.showMessageDialog(this, amount + "원 결제 완료!");
                } else if (rdoTransfer.isSelected()) {
                    String targetAcc = txtTargetAcc.getText();
                    bankService.transfer(myAcc, pw, targetAcc, amount);
                    JOptionPane.showMessageDialog(this, targetAcc + " 계좌로 " + amount + "원 이체 완료!");
                }
                refreshAdminTable(); 
                txtAmount.setText(""); txtPw.setText(""); txtTargetAcc.setText("");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "금액은 숫자로 입력해주세요.", "입력 오류", JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "거래 실패", JOptionPane.ERROR_MESSAGE);
            }
        });

        panel.add(actionPanel, BorderLayout.CENTER);
        return panel;
    }

    // ====================================================
    // [탭 3] 거래 내역 조회
    // ====================================================
    private JPanel createHistoryTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField txtAccNo = new JTextField(10);
        JPasswordField txtPw = new JPasswordField(10);
        JButton btnSearch = new JButton("조회");

        searchPanel.add(new JLabel("계좌번호: ")); searchPanel.add(txtAccNo);
        searchPanel.add(new JLabel(" 비밀번호: ")); searchPanel.add(txtPw);
        searchPanel.add(btnSearch);

        String[] colNames = {"거래 일시", "거래 종류", "상대 계좌", "거래 금액", "거래 후 잔액"};
        historyTableModel = createNonEditableModel(colNames); // 🌟 수정 방지 모델 적용
        JTable table = createReadableTable(historyTableModel);
        
        panel.add(searchPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        // 거래 내역 조회 이벤트
        btnSearch.addActionListener(e -> {
            try {
                String accNo = txtAccNo.getText();
                String pw = new String(txtPw.getPassword());
                
                BankAccount account = bankService.findAccount(accNo);
                if (account == null) throw new IllegalArgumentException("해당 계좌를 찾을 수 없습니다.");
                if (!account.checkPassword(pw)) throw new IllegalArgumentException("비밀번호가 틀렸습니다.");

                List<Transaction> transactions = account.getTransactions();
                historyTableModel.setRowCount(0); 
                
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

                for (Transaction t : transactions) {
                    // 영어로 된 거래 종류를 한글로 변환
                    String typeKor = "";
                    switch (t.getType().name()) {
                        case "DEPOSIT": typeKor = "입금"; break;
                        case "WITHDRAW": typeKor = "출금"; break;
                        case "PAYMENT": typeKor = "결제"; break;
                        case "TRANSFER_IN": typeKor = "이체(입금)"; break;
                        case "TRANSFER_OUT": typeKor = "이체(출금)"; break;
                        default: typeKor = t.getType().name();
                    }

                    historyTableModel.addRow(new Object[]{
                        t.getTransactionTime().format(formatter),
                        typeKor, // 원래 t.getType() 이었던 부분을 typeKor 로 변경!
                        t.getTargetAccountNumber(),
                        t.getAmount() + "원",
                        t.getBalanceAfterTransaction() + "원"
                    });
                }
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "조회 실패", JOptionPane.ERROR_MESSAGE);
                historyTableModel.setRowCount(0);
            }
        });

        return panel;
    }

    // ====================================================
    // 🛠️ 유틸리티 메서드
    // ====================================================
    private JTable createReadableTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setFont(MAIN_FONT);
        table.setRowHeight(30);
        table.setShowVerticalLines(true);
        table.setShowHorizontalLines(true);
        table.setIntercellSpacing(new Dimension(10, 0));
        table.setGridColor(Color.LIGHT_GRAY);
        table.getTableHeader().setFont(TITLE_FONT);
        table.getTableHeader().setPreferredSize(new Dimension(100, 35));
        
        // 셀 내용 가운데 정렬 (보기 좋게)
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for(int i = 0; i < table.getColumnCount(); i++){
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        
        return table;
    }

    private void refreshAdminTable() {
        adminTableModel.setRowCount(0); 
        for (BankAccount acc : bankService.getAllAccounts()) {
            adminTableModel.addRow(new Object[]{
                acc.getAccountNumber(), 
                acc.getAccountHolder(), 
                acc.getBalance() + "원", 
                acc.getOverdraftLimit() + "원"
            });
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MiniBankGUI().setVisible(true);
        });
    }
}