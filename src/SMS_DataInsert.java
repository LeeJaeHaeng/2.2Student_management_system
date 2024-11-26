/**
 * 학생정보관리시스템 구현(SMS_DataInsert.java)
 * 작성자: 2023243119 컴퓨터공학부 이재행
 * 코드 참고: Google, Claude Ai(sonnet 3.5), youtube, 기초프로젝트2 ppt, 강의영상
  1. 데이터베이스 초기 설정
    - MySQL 연결 설정
    - university 데이터베이스 생성
    - 테이블(users, departments, students) 생성
   2. 테이블 생성
    - users 테이블: 로그인 사용자 정보(id, password)
    - departments 테이블: 학과 정보(dname)
    - students 테이블: 학생 정보(이름, 학번, 학년, 구분, 학과)
   3. 데이터 삽입
    - users.txt 파일에서 사용자 정보 읽어서 users 테이블에 삽입 ("//" 구분자)
    - 11개 학과 정보를 departments 테이블에 삽입
    - students.txt 파일에서 학생 정보 읽어서 students 테이블에 삽입 (공백 구분자)
   4. 파일처리
    - BufferedReader로 파일 읽기
    - StringTokenizer로 데이터 파싱
    - INSERT 쿼리로 데이터베이스에 삽입
 */

import java.sql.*;
import java.io.*;
import java.util.StringTokenizer;
import java.nio.charset.StandardCharsets;

public class SMS_DataInsert {
    // 데이터베이스 연결 관련 상수
    private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String BASE_URL = "jdbc:mysql://localhost:3306?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Seoul";
    private static final String DB_URL = "jdbc:mysql://localhost:3306/university?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Seoul";
    private static final String USER = "root";
    private static final String PASS = "0000";  // MySQL 비밀번호

    // 파일 경로 상수
    private static final String USERS_FILE = "C:\\Users\\leejh\\OneDrive\\바탕 화면\\2-2학기\\기초프로젝트2\\개인프로젝트\\users.txt";
    private static final String STUDENTS_FILE = "C:\\Users\\leejh\\OneDrive\\바탕 화면\\2-2학기\\기초프로젝트2\\개인프로젝트\\students.txt";

    // 데이터베이스 연결 객체들
    private Connection conn = null;
    private Statement stmt = null;


     // 메인 메소드
    public static void main(String[] args) {
        SMS_DataInsert app = new SMS_DataInsert();
        app.execute();
    }

    //데이터베이스 연결 메소드

    //MYSQL 연결 및 데이터베이스 생성

    private void connectDB() {
        try {
            // JDBC 드라이버 로드
            Class.forName(JDBC_DRIVER);
            System.out.println("MySQL 드라이버 로딩 성공");

            // 먼저 기본 URL로 연결하여 데이터베이스 생성
            try (Connection baseConn = DriverManager.getConnection(BASE_URL, USER, PASS);
                 Statement baseStmt = baseConn.createStatement()) {

                String createDB = "CREATE DATABASE IF NOT EXISTS university";
                baseStmt.executeUpdate(createDB);
                System.out.println("데이터베이스 생성 완료");
            }

            // university 데이터베이스로 다시 연결
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            stmt = conn.createStatement();
            System.out.println("데이터베이스 연결 성공");

        } catch (Exception e) {
            System.out.println("데이터베이스 연결 오류: " + e.getMessage());
            System.exit(1);
        }
    }


    // 데이터베이스 연결 종료 메소드

    private void disconnectDB() {
        try {
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
            System.out.println("데이터베이스 연결 종료");
        } catch (SQLException e) {
            System.out.println("데이터베이스 종료 오류: " + e.getMessage());
        }
    }

     // 전체 실행 메소드

    public void execute() {
        connectDB();
        try {
            // 1. 기존 테이블 삭제
            dropTables();

            // 2. 새 테이블 생성
            createTables();

            // 3. 데이터 삽입
            insertDepartments();
            insertUsers();
            insertStudents();

        } catch (Exception e) {
            System.out.println("실행 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
        } finally {
            disconnectDB();
        }
    }


     // 기존 테이블 삭제 메소드

    private void dropTables() throws SQLException {
        // 외래키 제약조건 때문에 삭제 순서 중요
        String[] dropQueries = {
                "DROP TABLE IF EXISTS students",
                "DROP TABLE IF EXISTS departments",
                "DROP TABLE IF EXISTS users"
        };

        for (String query : dropQueries) {
            stmt.executeUpdate(query);
        }
        System.out.println("기존 테이블 삭제 완료");
    }

    // 테이블 생성 메소드

    private void createTables() throws SQLException {
        // users 테이블 생성 (로그인 사용자 정보)
        String createUsers =
                "CREATE TABLE users (" +
                        "id VARCHAR(20) PRIMARY KEY, " +
                        "password VARCHAR(20) NOT NULL" +
                        ") DEFAULT CHARSET=utf8";

        // departments 테이블 생성 (학과 정보)
        String createDepartments =
                "CREATE TABLE departments (" +
                        "dname VARCHAR(30) PRIMARY KEY" +
                        ") DEFAULT CHARSET=utf8";

        // students 테이블 생성 (학생 정보)
        String createStudents =
                "CREATE TABLE students (" +
                        "sname VARCHAR(20) NOT NULL, " +
                        "snum VARCHAR(20) PRIMARY KEY, " +
                        "year INT NOT NULL, " +
                        "grade VARCHAR(20) NOT NULL, " +  // 학부생,대학원생 구분
                        "department VARCHAR(30) NOT NULL, " +
                        "FOREIGN KEY (department) REFERENCES departments(dname)" +
                        ") DEFAULT CHARSET=utf8";

        // 테이블 생성 실행
        stmt.executeUpdate(createUsers);
        stmt.executeUpdate(createDepartments);
        stmt.executeUpdate(createStudents);
        System.out.println("테이블 생성 완료");
    }

    //학과 데이터 삽입 메소드

    private void insertDepartments() throws SQLException {
        // 요구사항에 명시된 11개 학과
        String[] departments = {
                "컴퓨터공학부", "전자공학과", "기계공학과", "건축공학과",
                "간호학과", "재료공학과", "경영학과", "일어일문학과",
                "산업경영공학과", "체육학과", "교육학과"
        };

        // 학과 데이터 삽입
        for (String dept : departments) {
            String query = String.format(
                    "INSERT INTO departments (dname) VALUES ('%s')",
                    dept
            );
            stmt.executeUpdate(query);
        }
        System.out.println("학과 데이터 삽입 완료");
    }

    // 사용자 데이터 삽입 메소드

    private void insertUsers() throws SQLException {
        try {
            // UTF-8 인코딩으로 파일 읽기
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(new FileInputStream(USERS_FILE), StandardCharsets.UTF_8)
            );
            String line;

            while ((line = br.readLine()) != null) {
                StringTokenizer st = new StringTokenizer(line, "//"); // "//" 구분자 사용
                String id = st.nextToken().trim();
                String password = st.nextToken().trim();
                // INSERT 쿼리 실행

                String query = String.format(
                        "INSERT INTO users (id, password) VALUES ('%s', '%s')",
                        id, password
                );
                stmt.executeUpdate(query);
            }
            br.close();
            System.out.println("사용자 데이터 삽입 완료");
        } catch (IOException e) {
            System.out.println("users.txt 파일 읽기 오류: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // 학생 데이터 삽입 메소드

    private void insertStudents() throws SQLException {
        try {
            // UTF-8 인코딩으로 파일 읽기
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(new FileInputStream(STUDENTS_FILE), StandardCharsets.UTF_8)
            );
            String line;

            while ((line = br.readLine()) != null) {
                StringTokenizer st = new StringTokenizer(line); // 공백구분, 데이터 파싱 및 INSERT 실행
                String department = st.nextToken();
                String year = st.nextToken();
                String name = st.nextToken();
                String grade = st.nextToken();
                String snum = st.nextToken();

                String query = String.format(
                        "INSERT INTO students (sname, snum, year, grade, department) " +
                                "VALUES ('%s', '%s', %s, '%s', '%s')",
                        name, snum, year, grade, department
                );
                stmt.executeUpdate(query);
            }
            br.close();
            System.out.println("학생 데이터 삽입 완료");
        } catch (IOException e) {
            System.out.println("students.txt 파일 읽기 오류: " + e.getMessage());
            e.printStackTrace();
        }
    }
}