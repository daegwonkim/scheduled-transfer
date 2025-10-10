<div align="center">
  <a href="https://git.io/typing-svg"><img src="https://readme-typing-svg.demolab.com?font=Black+Han+Sans&size=40&duration=2000&pause=1000&color=A041F7&center=true&vCenter=true&width=600&height=70&lines=Kafka%EB%A5%BC+%ED%99%9C%EC%9A%A9%ED%95%9C+%EC%98%88%EC%95%BD%EC%9D%B4%EC%B2%B4+%EC%84%9C%EB%B9%84%EC%8A%A4" alt="Typing SVG" /></a>
</div>

## 0. 프로젝트 개요 
예약이체 서비스는 **사용자가 미리 지정한 시점에 자동으로 계좌 이체를 수행하는 기능**을 제공합니다.  
예를 들어, 사용자가 “내일 오전 9시, 10만원을 송금”하도록 예약하면, 시스템은 해당 시점에 이체 요청을 자동으로 처리합니다.  

모든 은행은 각자의 **점검 시간**이 있으며, 이 시간 동안에는 송금이 불가능합니다.  
이로 인해 많은 사용자의 예약 이체 요청이 은행 점검 종료 직후 특정 시점에 몰릴 수 있습니다.  
즉, 수많은 이체 요청이 동시에 발생하면서, 트랜잭션 처리가 지연되고 서버가 과부화되며 일부 요청이 누락되거나 중복 실행될 가능성도 존재합니다.  

해당 프로젝트에서는 이러한 문제를 해결하기 위해 단순 스케줄러 기반 방식 대신 Kafka를 활용한 메시지 큐 기반의 비동기 처리 구조 사용하여 대량의 이체 요청을 안정적이고 확장성 있게 처리할 수 있도록 구현합니다.

## 1. 주요 목표
- 대량 이체 요청에 대한 안정적인 처리
- 중복 이체 방지
- 이체 상태 관리

## 2. 기술 스택
- **Language**: Java 17+
- **Framework**: Spring Boot 3.x
- **Database**: MySQL 8.0
- **Test**: json-server

## 3. 흐름도
<img width="851" height="359" alt="Image" src="https://github.com/user-attachments/assets/f0f5f8a6-e2c2-4da7-bd5d-2c8c558a3e75" />

## 4. 프로젝트 구조
```
scheduled_transfer/
├── domain/ # 도메인 관리
│   ├── src/
│   │   └── main/
│   │      ├── java/
│   │      │   └── io.github.daegwon.scheduled_transfer/
│   │      │       ├── service/         # 도메인 로직
│   │      │       ├── entity/          # JPA 엔티티 클래스
│   │      │       ├── repository/      # 데이터 접근 계층
│   │      │       └── dto/             # DTO
│   │      └── resources/
│   │          └── application-domain.yml  # JPA 설정
│   └── build.gradle.kts
├── infra/  # 외부 연동
│   ├── src/
│   │   └── main/
│   │      ├── java/
│   │      │   └── io.github.daegwon.scheduled_transfer/
│   │      │       ├── kafka/         # Kafka 관련 로직
│   │      │       └── service/       # 외부 API 호출용 서비스
│   │      └── resources/
│   │          └── application-infra.yml  # Kafka 설정
│   ├── build.gradle.kts
│   ├── core-banking.json    # json-server 용 응답 파일
│   └── docker-compose.yml   # Kafka 서버 띄우기 용
├── batch/  # 스케줄러
│   ├── src/
│   │   └── main/
│   │      ├── java/
│   │      │   └── io.github.daegwon.scheduled_transfer/
│   │      │       ├── scheduler/                # 스케줄러 로직
│   │      │       └── BatchApplication.class    # SpringBootApplication 클래스
│   │      └── resources/
│   │          └── application-domain.yml  # 스케줄러 설정
│   └── build.gradle.kts
├── build.gradle.kts
└── settings.gradle.kts
```
