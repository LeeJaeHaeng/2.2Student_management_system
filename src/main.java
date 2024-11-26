/**
 * 학생정보관리시스템 구현(main.java)
 * 작성자: 2023243119 컴퓨터공학부 이재행
 * 코드 참고: Google, Claude Ai(sonnet 3.5), youtube, 기초프로젝트2 ppt, 강의영상
   1. 로그인 시스템
    - 프로그램 시작 시 로그인 창 표시
    - ID/PW 입력받아 DB와 대조
    - 회원가입 기능 제공
   2. 메인 화면 구성
    - 왼쪽: 학과 목록과 학년별 트리 구조
    - 오른쪽: 학생 정보 테이블
    - 상단: 학부생/대학원생 필터 체크박스
    - 메뉴바: 학생등록, 삭제, 종료 기능
   3. 학생정보관리
    - 학과/학년 선택 시 해당 학생 목록 표시
    - 학생 등록/삭제 기능
    - 학부생/대학원생 구분 필터링
   4. 데이터베이스 연동
    - MySQL 연결 관리
    - 학생정보 조회/수정/삭제 쿼리 실행
 */

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.sql.*;

public class main extends JFrame {
    // DB 연결 정보
    private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String DB_URL = "jdbc:mysql://localhost:3306/university?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Seoul";
    private static final String USER = "root";
    private static final String PASS = "0000";

    // GUI 컴포넌트
    private JList<String> departmentList; // 학과목록
    private JTree yearTree; // 학년트리
    private JTable studentTable; // 학생 정보 테이블
    private JCheckBox undergradCheck, gradCheck; // 학생 구분 체크박스
    private JFrame loginFrame;
    private Connection conn = null;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new main().showLogin();
        });
    }

    // 로그인 화면
    private void showLogin() {
        connectDB();  // DB 연결

        loginFrame = new JFrame("Login");
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loginFrame.setSize(300, 150);
        loginFrame.setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));
        JTextField idField = new JTextField();
        JPasswordField pwField = new JPasswordField();
        JButton loginBtn = new JButton("로그인");
        JButton registerBtn = new JButton("회원가입");

        panel.add(new JLabel("ID:"));
        panel.add(idField);
        panel.add(new JLabel("Password:"));
        panel.add(pwField);
        panel.add(loginBtn);
        panel.add(registerBtn);

        loginBtn.addActionListener(e -> {
            if (checkLogin(idField.getText(), new String(pwField.getPassword()))) {
                loginFrame.dispose();
                showMainFrame();
            } else {
                JOptionPane.showMessageDialog(loginFrame, "로그인 실패!");
            }
        });

        registerBtn.addActionListener(e -> showRegisterDialog());

        loginFrame.add(panel);
        loginFrame.setVisible(true);
    }

    // DB 연결
    private void connectDB() {
        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "DB 연결 실패: " + e.getMessage());
            System.exit(1);
        }
    }

    // 로그인 체크
    private boolean checkLogin(String id, String password) {
        try {
            String query = "SELECT * FROM users WHERE id=? AND password=?";
            PreparedStatement pstmt = conn.prepareStatement(query); // ID/PW 검증 로직
            pstmt.setString(1, id);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 회원가입 화면
    private void showRegisterDialog() {
        JDialog dialog = new JDialog(loginFrame, "회원가입", true);
        dialog.setLayout(new GridLayout(3, 2, 5, 5));
        dialog.setSize(250, 150);

        JTextField newId = new JTextField();
        JPasswordField newPw = new JPasswordField();
        JButton confirmBtn = new JButton("회원가입");

        dialog.add(new JLabel("사용할 ID:"));
        dialog.add(newId);
        dialog.add(new JLabel("사용할 Password:"));
        dialog.add(newPw);
        dialog.add(new JLabel(""));
        dialog.add(confirmBtn);

        confirmBtn.addActionListener(e -> {
            if (registerUser(newId.getText(), new String(newPw.getPassword()))) {
                dialog.dispose();
            }
        });

        dialog.setLocationRelativeTo(loginFrame);
        dialog.setVisible(true);
    }

    // 회원가입
    private boolean registerUser(String id, String password) {
        try {
            String query = "INSERT INTO users (id, password) VALUES (?, ?)"; // 회원가입 처리
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, id);
            pstmt.setString(2, password);
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(null, "회원가입 성공!");
            return true;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "회원가입 실패: " + e.getMessage());
            return false;
        }
    }

    // 메인 프레임 표시
    private void showMainFrame() {
        setTitle("학생정보관리시스템_이재행");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        // 왼쪽 패널 (학과 목록 + 트리)
        JPanel leftPanel = new JPanel(new BorderLayout());
        createDepartmentList();
        createYearTree();
        leftPanel.add(new JScrollPane(departmentList), BorderLayout.NORTH);
        leftPanel.add(new JScrollPane(yearTree), BorderLayout.CENTER);

        // 오른쪽 패널 (필터 + 테이블)
        JPanel rightPanel = new JPanel(new BorderLayout());
        createFilterPanel(rightPanel);
        createStudentTable(rightPanel);

        // 메뉴바 설정
        setJMenuBar(createMenuBar());

        // 메인 레이아웃
        setLayout(new BorderLayout());
        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.CENTER);

        setVisible(true);
    }

    // 학과 목록 생성
    private void createDepartmentList() {
        DefaultListModel<String> model = new DefaultListModel<>();
        departmentList = new JList<>(model);

        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT dname FROM departments");
            while (rs.next()) {
                model.addElement(rs.getString("dname"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        departmentList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateYearTree();
            }
        });
    }

    // 학년 트리 생성
    private void createYearTree() {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("학과를 선택하세요");
        yearTree = new JTree(root);
        yearTree.addTreeSelectionListener(e -> updateStudentTable());
    }

    // 학년 트리 업데이트
    private void updateYearTree() {
        String selectedDept = departmentList.getSelectedValue();
        if (selectedDept == null) return;

        DefaultMutableTreeNode root = new DefaultMutableTreeNode(selectedDept);
        for (int i = 1; i <= 4; i++) {
            root.add(new DefaultMutableTreeNode(i + "학년"));
        }

        yearTree.setModel(new DefaultTreeModel(root));
    }

    // 필터 패널 생성
    private void createFilterPanel(JPanel rightPanel) {
        JPanel filterPanel = new JPanel();
        undergradCheck = new JCheckBox("학부생");
        gradCheck = new JCheckBox("대학원생");

        undergradCheck.setSelected(true);
        gradCheck.setSelected(true);

        undergradCheck.addActionListener(e -> updateStudentTable());
        gradCheck.addActionListener(e -> updateStudentTable());

        filterPanel.add(undergradCheck);
        filterPanel.add(gradCheck);
        rightPanel.add(filterPanel, BorderLayout.NORTH);
    }

    // 학생 테이블 생성
    private void createStudentTable(JPanel rightPanel) {
        String[] columns = {"학번", "이름", "학년", "구분", "학과"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        studentTable = new JTable(model);
        rightPanel.add(new JScrollPane(studentTable), BorderLayout.CENTER);
    }

    // 메뉴바 생성
    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu mainMenu = new JMenu("Main");

        JMenuItem registerItem = new JMenuItem("학생등록");
        JMenuItem deleteItem = new JMenuItem("학생삭제");
        JMenuItem exitItem = new JMenuItem("종료");

        registerItem.addActionListener(e -> showStudentRegisterDialog());
        deleteItem.addActionListener(e -> showStudentDeleteDialog());
        exitItem.addActionListener(e -> {
            closeDB();
            System.exit(0);
        });

        mainMenu.add(registerItem);
        mainMenu.add(deleteItem);
        mainMenu.addSeparator();
        mainMenu.add(exitItem);
        menuBar.add(mainMenu);

        return menuBar;
    }

    // 학생 등록 다이어로그
    private void showStudentRegisterDialog() {
        JDialog dialog = new JDialog(this, "학생 등록", true);
        dialog.setSize(300, 200);
        dialog.setLayout(new GridLayout(6, 2, 5, 5));

        JTextField nameField = new JTextField();
        JTextField numField = new JTextField();
        JTextField yearField = new JTextField();
        String[] grades = {"학부생", "대학원생"};
        JComboBox<String> gradeCombo = new JComboBox<>(grades);
        JComboBox<String> deptCombo = new JComboBox<>();

        // 학과 콤보박스 데이터 로드
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT dname FROM departments");
            while (rs.next()) {
                deptCombo.addItem(rs.getString("dname"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        JButton confirmBtn = new JButton("등록");

        dialog.add(new JLabel(" 이름:"));
        dialog.add(nameField);
        dialog.add(new JLabel(" 학번:"));
        dialog.add(numField);
        dialog.add(new JLabel(" 학년(1-4):"));
        dialog.add(yearField);
        dialog.add(new JLabel(" 구분:"));
        dialog.add(gradeCombo);
        dialog.add(new JLabel(" 학과:"));
        dialog.add(deptCombo);
        dialog.add(new JLabel(""));
        dialog.add(confirmBtn);

        confirmBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            String num = numField.getText().trim();
            String year = yearField.getText().trim();

            if (name.isEmpty() || num.isEmpty() || year.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "모든 항목을 입력해주세요.");
                return;
            }

            try {
                int yearNum = Integer.parseInt(year);
                if (yearNum < 1 || yearNum > 4) {
                    JOptionPane.showMessageDialog(dialog, "학년은 1-4 사이의 숫자여야 합니다.");
                    return;
                }

                if (registerStudent(
                        name,
                        num,
                        year,
                        (String) gradeCombo.getSelectedItem(),
                        (String) deptCombo.getSelectedItem()
                )) {
                    dialog.dispose();  // 등록 성공 시 다이얼로그를 닫기
                    updateStudentTable();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "학년은 숫자여야 합니다.");

            }
        });

        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    // 학생 등록
    private boolean registerStudent(String name, String num, String year, String grade, String dept) {
        try {
            String query = "INSERT INTO students (sname, snum, year, grade, department) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, name);
            pstmt.setString(2, num);
            pstmt.setInt(3, Integer.parseInt(year));
            pstmt.setString(4, grade);
            pstmt.setString(5, dept);
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "학생 등록 성공!");
            return true;  // 등록 성공 시 true 반환
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "학생 등록 실패: " + e.getMessage());
            return false;  // 등록 실패 시 false 반환
        }
    }

    // 학생 삭제 다이얼로그
    private void showStudentDeleteDialog() {
        String snum = JOptionPane.showInputDialog(this, "삭제할 학생의 학번을 입력하세요:");
        if (snum != null && !snum.trim().isEmpty()) {
            deleteStudent(snum.trim());
            updateStudentTable();
        }
    }

    // 학생 삭제
    private void deleteStudent(String snum) {
        try {
            String query = "DELETE FROM students WHERE snum = ?"; // 삭제처리
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, snum);
            int result = pstmt.executeUpdate();

            if (result > 0) {
                JOptionPane.showMessageDialog(this, "학생 삭제 성공!");
            } else {
                JOptionPane.showMessageDialog(this, "해당 학번의 학생이 없습니다.");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "학생 삭제 실패: " + e.getMessage());
        }
    }

    // 학생 테이블 업데이트
    private void updateStudentTable() {
        DefaultTableModel model = (DefaultTableModel) studentTable.getModel();
        model.setRowCount(0);  // 테이블 초기화

        String selectedDept = departmentList.getSelectedValue();
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) yearTree.getLastSelectedPathComponent();
        if (selectedDept == null || node == null) return;

        try {
            StringBuilder query = new StringBuilder();
            query.append("SELECT * FROM students WHERE department = ?");

            // 학년 필터
            if (!node.isRoot()) {
                String yearStr = node.toString().replace("학년", "").trim();
                query.append(" AND year = ").append(yearStr);
            }

            // 학부생/대학원생 필터
            if (undergradCheck.isSelected() && !gradCheck.isSelected()) {
                query.append(" AND grade = '학부생'");
            } else if (!undergradCheck.isSelected() && gradCheck.isSelected()) {
                query.append(" AND grade = '대학원생'");
            } else if (!undergradCheck.isSelected() && !gradCheck.isSelected()) {
                return;  // 둘 다 선택되지 않은 경우 아무것도 표시하지 않음
            }

            PreparedStatement pstmt = conn.prepareStatement(query.toString());
            pstmt.setString(1, selectedDept);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getString("snum"),
                        rs.getString("sname"),
                        rs.getInt("year"),
                        rs.getString("grade"),
                        rs.getString("department")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "데이터 조회 실패: " + e.getMessage());
        }
    }

    // 프로그램 종료 시 DB 연결 종료
    private void closeDB() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
                System.out.println("데이터베이스 연결이 종료되었습니다.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 윈도우 종료 시 호출되는 메소드
    @Override
    protected void processWindowEvent(WindowEvent e) {
        super.processWindowEvent(e);
        if (e.getID() == WindowEvent.WINDOW_CLOSING) {
            closeDB();
            System.exit(0);
        }
    }
}