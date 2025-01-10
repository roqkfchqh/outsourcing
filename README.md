# 아웃소싱 프로젝트

아웃소싱 프로젝트란?

기업이나 조직이 특정 업무나 프로젝트를 외부 전문 업체나 프리랜서에게 위탁해 수행하는 방식으로, 내부에서 처리하기 어려운 작업을 외부 리소스로 해결하는 것입니다.

`배달 어플리케이션` 개발에 대한 아웃소싱 프로젝트를 맡았다고 가정해봅시다.

🏁 클라이언트는 배달 시장에 새롭게 진출하려는 스타트업으로, 내부에 개발 인력이 부족한 상황에서 전체 개발 과정을 외주로 맡기기로 결정했습니다. 

🚨 기한은 단 일주일입니다! 빨리 개발하지 않으면 클라이언트에게 위약금을 물어주어야합니다!

[팀 노션 구경하러 가기](https://teamsparta.notion.site/14-c9fdccf160d34d0585806b97a0f48cf1)

[프로젝트 시연 영상 보러가기]()

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
| [FULLTEXT 검색으로 성능 개선](https://teamsparta.notion.site/FULLTEXT-8d9c64e8ddab4c9f920ec22108f5801a) | `FULLTEXT INDEX` `MySQL`|
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
