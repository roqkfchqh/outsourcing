# 채찍의 민족

기사님 10분 전에 출발했다면서 왜 배달이 안와요!

조리 중, 배달 시작 알림들을 실시간으로 확인할 수 있어 게으름 피우는 배달기사들을 채찍질 할 수 있습니다.

알림, 장바구니, 주문, 검색 기능에 더해서 가게 사장님들을 위한 가게 관리 서비스도 제공합니다.

[팀 노션 구경하러 가기](https://teamsparta.notion.site/14-c9fdccf160d34d0585806b97a0f48cf1)

[프로젝트 시연 영상 보러가기](https://youtu.be/qZMLSx5deLk)

[발표자료 보기](https://drive.google.com/file/d/1TxLxV-lgu7zfRcA1e1bjaZ_g0KkcsLJW/view?usp=sharing)

<br/>

### 목차

- [🏝 프로젝트](#-프로젝트)
- [⚙ 주요 기능](#-주요-기능)
- [📊 ERD 설계](#-erd-설계)
- [🌐 Api 명세서](#-api-명세서)
- [📝 개발 일지](#-개발-일지)
- [👨‍👩‍👧‍👦 Members](#-members)


<br/>

### 🏝 프로젝트

- **프로젝트 기간**: 2025.01.07 ~ 2025.01.13  

- **기술 스택**:
  - Spring Boot 3.4.0  
  - Spring Data JPA
  - Spring Cache
  - Spring AOP
  - Spring Scheduler
  - QueryDSL
  - JWT
  - Web Socket
  - MySQL 8.0

<br/>

### ⚙ 주요 기능

- 회원가입/로그인
- 가게 관리
- 메뉴 관리
- 메뉴 주문하기
- 장바구니
- 리뷰 작성
- 통합 검색
- 알림

<br>

### 📊 ERD 설계

<div align="center"><img src="https://github.com/user-attachments/assets/148c8b1c-c66c-4940-9e11-1236cb446a3b"/></div>

<br/>

### 🌐 Api 명세서

[상세api확인을 위해 여기를 클릭해주세요](https://docs.google.com/spreadsheets/d/1lB1UFTQbnEZ7na9wkwy5F72Nc7NuMdDJpK_-VRaNiho/edit)

<br/>

### 📝 개발 일지

| 포스트                       | 태그            |
| ----------------------------- | ------------- |
| [리팩토링 일기: SRP 최대한 준수해보자](https://teamsparta.notion.site/SRP-5067f5ab5b2c420b9e30f2b06df4e7b5) | `SRP` `Refactoring` |
| [AOP로 인가 관심사 분리하기](https://teamsparta.notion.site/AOP-244679c937a348a2a0ba9ac3ff085ac4) | `AOP` `Pointcut` |
| [Spring Scheduler로 DB 주기적으로 업데이트 하기](https://teamsparta.notion.site/Spring-Scheduler-DB-e9aeb4d3875946e696c2882373511ba6) | `Spring Scheduler` `Cron` |
| [FULLTEXT 검색으로 성능 개선](https://teamsparta.notion.site/FULLTEXT-8d9c64e8ddab4c9f920ec22108f5801a) | `FULLTEXT INDEX` `MySQL`|
| [HQL Parsing 에러](https://teamsparta.notion.site/HQL-Parsing-0e8ef1ebe82e4b568f144cd18db41b8a) | `QueryDSL` `Dialect` |
| [WebSocket + AOP로 알림 기능 구현](https://teamsparta.notion.site/WebSocket-AOP-5298c88d3d584a07a27fa1f006554ccd) | `AOP` `WebSocket` |
| [QueryDSL로 통합검색 개발하기](https://teamsparta.notion.site/QueryDSL-013b19ffe4a14797808dbe22c22a3d55) |  `QueryDSL` `Fetch Join`|
| [JWT 에서의 로그아웃](https://teamsparta.notion.site/JWT-8402baa778aa4a12b36bdd352eafa4b1) | `JWT` `BlackList Logout` |
| [스프링 캐시에서 내부 호출 문제](https://teamsparta.notion.site/7ba57bdf7c0e412a9afbd533b3ecf614) | `Proxy` `Spring Cache` |
| [아웃소싱(배달앱) 개발 전, AOP에 대하여(with. Jwt token)](https://teamsparta.notion.site/AOP-with-Jwt-token-8b5b4d4b57b4487790a7f07b97d51af5) | `AOP` `JWT` |
| [Spring Cache로 장바구니 구현](https://teamsparta.notion.site/Spring-Cache-fe1f7b97598b4d7cb07b6e976d99ab6c) | `Spring Cache` `AOP` |


<br/>

### 👨‍👩‍👧‍👦 Members

<table align="center">
    <thead>
        <tr>
            <th>👑 팀장</th>
            <th>팀원</th>
            <th>팀원</th>
            <th>팀원</th>
        </tr>
    </thead>
    <tbody>
        <tr>
            <td align="center"><a href="https://github.com/roqkfchqh"><img src="https://github.com/roqkfchqh.png" width="100px;" alt=""/></a></td>
            <td align="center"><a href="https://github.com/yeongbinim"><img src="https://github.com/yeongbinim.png" width="100px;" alt=""/></a></td>
            <td align="center"><a href="https://github.com/juyangjin"><img src="https://github.com/juyangjin.png" width="100px;" alt=""/></a></td>
            <td align="center"><a href="https://github.com/Park-Junho1"><img src="https://github.com/Park-Junho1.png" width="100px;" alt=""/></a></td>
        </tr>
        <tr>
            <td align="center">이채영</td>
            <td align="center">임영빈</td>
            <td align="center">진주양</td>
            <td align="center">박준호</td>
        </tr>
    </tbody>
</table>
