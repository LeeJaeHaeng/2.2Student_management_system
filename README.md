# 학생 관리 시스템 (Student Management System)

## 프로젝트 개요
이 프로젝트는 Java로 개발된 학생 관리 시스템으로, 교육기관에서 학생 정보를 효율적으로 관리하기 위한 솔루션입니다. 학생 데이터의 입력, 조회, 수정, 삭제(CRUD) 기능을 제공하며, 사용자 친화적인 인터페이스를 통해 학생 정보를 쉽게 관리할 수 있습니다.

## 주요 기능
- **학생 정보 관리**: 학생의 기본 정보(이름, 학번, 연락처, 이메일 등) 등록 및 관리
- **과목 및 성적 관리**: 학생별 수강 과목과 성적 기록 및 조회
- **검색 및 필터링**: 다양한 조건을 통한 학생 정보 검색
- **보고서 생성**: 학생 성적표, 통계 보고서 등 출력
- **데이터 영속성**: 모든 정보의 안전한 저장 및 복구

## 기술 스택
- **개발 환경**: Eclipse IDE
- **프로그래밍 언어**: Java
- **데이터베이스**: MySQL/H2/내장 DB
- **데이터 접근**: JDBC
- **사용자 인터페이스**: Java Swing/AWT
- **아키텍처 패턴**: MVC(Model-View-Controller) 패턴

## 시스템 요구사항
- JDK 8 이상
- Eclipse IDE
- 데이터베이스 서버(선택적)

## 설치 및 실행 방법
1. 이 저장소를 클론합니다:
   ```
   git clone https://github.com/LeeJaeHaeng/2.2Student_management_system.git
   ```
2. Eclipse IDE를 실행합니다.
3. `File > Import > Existing Projects into Workspace`를 선택합니다.
4. 클론한 프로젝트 디렉토리를 선택하고 `Finish`를 클릭합니다.
5. 프로젝트를 우클릭하고 `Run As > Java Application`을 선택하여 애플리케이션을 실행합니다.

## 사용 방법
1. 애플리케이션 실행 후 메인 인터페이스가 표시됩니다.
2. 메뉴에서 원하는 기능을 선택합니다:
   - 학생 등록: 새로운 학생 정보 입력
   - 학생 조회: 등록된 학생 목록 확인
   - 학생 검색: 특정 조건으로 학생 검색
   - 정보 수정: 기존 학생 정보 업데이트
   - 학생 삭제: 학생 정보 삭제
   - 보고서 생성: 다양한 보고서 출력

## 프로젝트 구조
```
src/
├── model/          # 데이터 모델 클래스 (Student, Course 등)
├── view/           # UI 컴포넌트 및 화면 구성 클래스
├── controller/     # 비즈니스 로직 및 데이터 처리 클래스
├── dao/            # 데이터 접근 객체 클래스
└── util/           # 유틸리티 및 헬퍼 클래스
```

## 데이터베이스 스키마
시스템은 다음과 같은 주요 테이블을 사용합니다:
- `students`: 학생 기본 정보
- `courses`: 과목 정보
- `enrollments`: 학생 수강 정보
- `grades`: 성적 정보

## 향후 개선 사항
- 웹 인터페이스 추가
- 모바일 앱 지원
- 데이터 분석 및 시각화 기능 강화
- 클라우드 백업 및 동기화

## 개발자
- 이재행 (LeeJaeHaeng)

## 라이선스
이 프로젝트는 MIT 라이선스 하에 배포됩니다.
