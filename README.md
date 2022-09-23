# 리폼 커뮤니티 리본

<img height="300px" src="https://user-images.githubusercontent.com/105181604/181456826-d342485e-99c7-4f0c-8e28-b8c9870b9195.png">

<img height="350px" src="https://user-images.githubusercontent.com/102746846/189794459-e26d2307-4ce4-4601-a6b3-8d3deb6d711f.png">

### [🎀 리본 바로가기 >](https://ribborn.kr)


   
### 당신의 옷장의 옷을 구해주세요! 
<br><br>

## 🛠️ 아키텍처

<br>
<!-- <details> -->
<!--     <summary>아키텍처 바로보기</summary> -->

<!-- summary 아래 한칸 공백 두고 내용 삽입 -->
![서비스아키텍처수정](https://user-images.githubusercontent.com/59018674/182071222-93c422c3-7169-46d3-b223-857d56fa8dfd.png)


<!-- </details> -->


   
## ❓ 기획 의도       
<br>




#### 수십톤씩 무차별적으로 버려지는 의류들로 인한 환경파괴 방지와 패션에 개성을 담고싶어하는 사람들을 위한 리폼 커뮤니티 사이트입니다.

  리폼 서비스를 필요로 하는 고객과 디자이너/전문가가 채팅으로 손쉽게 만나고
  리폼에 대한 지식을 공유할 수 있는 커뮤니티 서비스입니다.


* 🤟[RIBBORN 서비스 바로가기](https://ribborn.kr/)
* 👀[서비스 시연 및 발표 영상 보러가기](유튜브 링크)

<br>

### Back-end 기술 스택
## 📜 기술스택
|분류|기술|
| :-: |:- |
|Language|<img src="https://img.shields.io/badge/JAVA-007396?style=for-the-badge&logo=java&logoColor=white">|
|Framework|<img src="https://img.shields.io/badge/Spring-6DB33F?style=for-the-badge&logo=Spring&logoColor=white"> <img src="https://img.shields.io/badge/Springboot-6DB33F?style=for-the-badge&logo=Springboot&logoColor=white">|
|Build Tool|<img src="https://img.shields.io/badge/gradle-02303A?style=for-the-badge&logo=gradle&logoColor=white">|
|DB|<img src="https://img.shields.io/badge/mysql-4479A1?style=for-the-badge&logo=mysql&logoColor=white">|
|Server|<img src="https://img.shields.io/badge/aws-232F3E?style=for-the-badge&logo=AmazonAWS&logoColor=white"> <img src="https://img.shields.io/badge/Amazon S3-569A31?style=for-the-badge&logo=Amazon S3&logoColor=white">|





## 🗓 프로젝트 기간

<br>

* 2022년 6월 24일 ~ 2022년 8월 5일 
  
<br>  

## ⛏️ ER 다이어그램

<br>

<details>
    <summary>ERD 바로보기</summary>

<!-- summary 아래 한칸 공백 두고 내용 삽입 -->
![ribborn (1)](https://user-images.githubusercontent.com/59018674/182074665-6bd5c496-4d1a-4847-911a-dd020ab3adcf.png)



</details>

## 🔥이슈 및 트러블슈팅

<br>

<details>
<summary><b>N + 1 문제</b></summary>
  
> **문제** : User, Post, Contents, Comment 엔티티는 N:1 맵핑이 되어있기 때문에 호출 시 N + 1 문제를 야기할 수 있음
>
> **해결** : 데이터를 Flat하게 조회해야 할 경우에는 Repository에서 DTO를 바로 생성하여 리턴했으나, 해당 방법은 페이징 처리가 되지 않기 때문에 페이징이 필요한 Comment는 API를 분리해서 따로 페이징 처리를 했음
  
</details>
<details>
<summary><b>게시글 내용이 길 경우 게시글이 작성되지 않는 문제</b></summary>
  
> **문제** : 게시글 내용이 길 경우 게시글이 작성되지 않고 rollback 처리 되는 문제 발생
>
> **해결** : 프론트단에서 글자 수를 제한하는 방법으로 생각했으나, 커뮤니티 서비스 특성상 글자수를 제한하는 것은 유저 입장에서 큰 불편함을 겪을 수 있기 때문에 nginx.conf 파일에서 client_max_body_size를 조정해서 처리했음
  
</details>

<details>
<summary><b>게시글에 이모지가 작성될 경우 에러가 발생하는 문제</b></summary>
  
> **문제** : 게시글,댓글,아이디 등 String이 들어가는 모든 문구에서 이모지가 들어갈 경우 HttpMessageNotWritableException 에러가 발생했음
>
> **해결** : 이모지는 2byte보다 크기가 크기 때문에 문제가 발생할 수 있다. XSS 필터를 추가해 해결할 수 있을 것 같다(22.08.01 현재 해결 x)
  
</details>

<details>
<summary><b>채팅 알림 설정 </b></summary>
  
> **문제** : 기존에 채팅을 웹소켓으로 구현하였는데 알림이라는것은 서버에서 클라이언트로만 데이터를 보내면되는 단방향통신만 있으면 충분할꺼라고 생각을하여 다른 통신 방법을 
   찾아봄
>
> **해결** : 단방향 데이터 통신방식인 SSE(server sent event) 방식을 선택하여 사용에 대한
  자세한 내용은 링크 들어가시면 좋습니다. [📝]( https://successful-spur-143.notion.site/0f7341351c50473292bc9648f532c7b0 )
  
</details>

<br>

## :hammer_and_wrench: 기능설명

<br>
<details>
<summary> ✅ 4종류의 게시판 CRUD</summary>
  
<div markdown="1">       

- 리폼 견적요청, 리폼 후기, 리폼 질문, 디자이너/전문가 룩북 4종류의 게시판 CRUD
- 이미지 업로드, 댓글CRUD, 게시글 좋아요 및 공유 가능

![3](https://user-images.githubusercontent.com/59018674/182070501-58f8ad90-c6e4-45eb-ad04-bc4e6066a114.PNG)
![4](https://user-images.githubusercontent.com/59018674/182070504-1e9e1701-7db6-4b9b-b6fa-fc50ca46ae79.PNG)
![5](https://user-images.githubusercontent.com/59018674/182070505-b29ad171-7a23-4bd0-a95e-57f045683852.PNG)
![6](https://user-images.githubusercontent.com/59018674/182070509-b51b75ab-54ba-4a17-8e47-b64880e29791.PNG)


</div>
</details>

<details>
<summary> ✅ 채팅 (Stomp, WebSocket)</summary>
  
<div markdown="1">       

- 전문가/디자이너, 일반유저간 1 : 1 채팅방 생성
- 채팅 내용이 분 단위로 구분됨
- 좌측 채팅목록에서 기존 채팅중이던 목록을 확인할 수 있음
- 채팅 중 견적 게시글에 대해 진행중, 완료 등으로 상태 변경 가능

![채팅](https://user-images.githubusercontent.com/59018674/182070598-098dff0a-1d85-4a7f-a543-747b73ad3874.png)

</div>
</details>

<details>
<summary> ✅ 알림 (Server sent event)</summary>
  
<div markdown="1">       

- 해당 채팅방에 있지 않거나 오프라인 상태 시, 알림 송신
  * 새로운 메시지 전달 시
  * 해당 견적 게시글의 상태가 변경될 시
  * 전문가/디자이너가 거래 취소 시
  
여기에 알림 이미지

</div>
</details>

<details>
<summary> ✅ 게시글의 인기순, 최신순, 지역, 진행정도로 정렬 가능</summary>
  
<div markdown="1">       
  
![image](https://user-images.githubusercontent.com/59018674/182070687-d319b7e2-dbf8-4352-be9c-bf221e97942b.png)
![image](https://user-images.githubusercontent.com/59018674/182070772-f3983d02-e2aa-4421-94ab-22200a739a4a.png)


</div>
</details>

<details>
<summary> ✅ 마이페이지 내 정보 조회 기능</summary>
  
- 본인이 작성한 게시글 / 북마크한 게시글을 확인 가능  
  
<div markdown="1">       
  
![image](https://user-images.githubusercontent.com/59018674/182070840-971b6ac3-d74f-49b8-b3ce-e6a5d1a525d1.png)

</div>
</details>

<details>
<summary> ✅ 마이페이지에서 내 정보 변경 기능</summary>
  
- 닉네임 및 개인정보 변경 가능
  
<div markdown="1">       
  
![image](https://user-images.githubusercontent.com/59018674/182070899-f14dcd71-11de-4aad-9152-8d43b600edfd.png)
  
</div>
</details>

<details>
<summary> ✅ 글 작성자의 게시글 확인 기능</summary>
  
- 작성자의 마이페이지에서 게시글 확인 가능 
  
<div markdown="1">       
  
![image](https://user-images.githubusercontent.com/59018674/182071064-215be52f-a653-4b91-b694-aadce3f2903b.png)
  
</div>
</details>

<br>

##  API 설계

[API 설계 자세히보기](https://www.notion.so/API-3078c33df93d4bc180531687a99b2757)

## 👥 팀 소개

Backend

<br>

팀원|github
---|---|
박성규 | https://github.com/ParkSungGyu1
이정우 | https://github.com/lky8967
박성렬 | https://github.com/songryel-park
<br>




