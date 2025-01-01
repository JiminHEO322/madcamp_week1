## 🏛️ YESUL : 문화 생활을 즐기고 싶으신가요?

![BananaCatCryingGIF.gif](BananaCatCryingGIF.gif)

- YESUL은 전시/공연 등의 정보를 보기 쉽게 알려줍니다.
- 주변 전시/공연장과 현재 진행 중인 전시/공연 정보를 한 눈에 볼 수 있습니다.
- 전시/공연을 관람한 후에는 나만의 리뷰를 작성하여 “평생” 보관할 수 있습니다.

### 개발 툴

- **IDE** : Android Studio
- **Language** : Kotlin
- **Design** : Figma
- **Coop** : Github

### 조원 소개

**허지민**

- 카이스트 전산학부 21학번
- https://github.com/JiminHEO322

**조어진**

- 유니스트 컴퓨터공학과 20학번
- https://github.com/gutsguy

---

## <Design Details>

### Splash Screen

<스플래시 스크린>

**앱 로고**

- 키스 해링의 작품을 오마주하여 앱의 로고를 디자인했습니다.

**스플래시 스크린**

- 뱅크시의 ‘사랑은 쓰레기통에’를 오마주하여 액자 안의 그림이 아래로 내려가며 파쇄되는 효과를 주었습니다.
    - 뱅크시 <사랑은 쓰레기통에>
        
        ![image.png](image.png)
        

### Tab 1. 전시/공연장 정보

1. **전시/공연장 리스트**
    - Tab1 에서는 전시/공연장의 정보들을 RecyclerView로 보여줍니다.
    - 모든 데이터는 json 파일에 저장되어 사용자의 상호작용을 통해 수정할 수 있습니다.
    - 초기에는 “가나다순”으로 정렬되며, 우측 상단의 버튼을 눌러 “거리순”으로 전환할 수 있습니다.
    - 각 View에는 전시/공연장의 사진과 이름, 대략적인 위치, 전화번호, 진행 중인 공연 수, 관심 등록 아이콘이 표시됩니다.
    - 관심 등록 아이콘을 누르면 관심 등록을 설정/해제할 수 있으며, 우측 상단의 하트 아이콘을 터치하여 관심있는 전시/공연장만 필터링 할 수 있습니다.
    - 상단의 검색 기능을 사용하여 전시/공연장 이름을 검색할 수 있습니다.
    
    gps 권한 얻는거부터 거리순 가나다순 전환하는거
    
    검색하는거랑 관심 버튼 누르고 전환하는거
    

1. **전시/공연장 상세 정보**
    - 각 아이템을 터치하면 상세 정보 창이 나옵니다.
    - 상세 정보에서는 전시/공연장의 전체 주소와 관심 등록 여부를 보여줍니다.
    - 전화, 홈페이지, 인스타그램, 유튜브 아이콘을 터치하면 전화 앱 또는 링크로 바로 이동할 수 있습니다. (없을 시에는 ToastMessage를 통해 표시)
    - 현재 진행 중인 공연/전시를 하단에 있는 RecyclerView의 GridLayout으로 보여줍니다.
    - 각 공연/전시 터치 시 상세 정보 페이지로 연결됩니다.

하트 클릭하는거랑 전화랑 인스타 들어가기

공연목록 스크롤하고 들어가보기기

### Tab 2. 현재 진행 중인 전시/공연 정보

1. **전시/공연 리스트**
    - Tab2 에서는 현재 진행 중인 전시와 공연들을 각각의 HorizontalRecyclerView로 보여줍니다.
    - 뒷 배경을 고급스러운 커튼으로 바꾸고, 하단 내비게이션 바를 투명하게 하여 실제로 미술관에서 작품을 보는 듯한 분위기를 연출했습니다.

탭 전환하는거부터 스크롤하기

1. **전시/공연 상세 정보**
    - 장소와 기간, 출연자 정보가 표시되고, 예매하기 버튼을 눌러 바로 예매 사이트에 접속할 수 있습니다.
    - 관람 후에 Review버튼을 누르면 Tab3에 공연이 추가되며, 버튼을 한번 더 누르면 리뷰 작성 페이지로 바로 이동할 수 있습니다.

예매하기 누르는거

리뷰 작성 페이지 들어가는거거

### Tab 3. 나만의 리뷰 작성

1. **리뷰 리스트**
    - 상단에는 user가 관람한 공연/전시의 개수가 카테고리 별로 나뉘어 표시됩니다.
    - 각 아이템을 터치하면 본인이 작성한 리뷰 글을 볼 수 있습니다.
    - 리뷰들은 최근 관람한 리뷰 순서대로 정렬됩니다.
2. **리뷰 페이지**
    - 본인이 관람한 공연에 대한 리뷰를 작성할 수 있습니다.
    - 상단의 수정 버튼을 누르면 리뷰 내용을 수정할 수 있습니다.
        - 사진 변경 버튼을 눌러 리뷰의 사진을 변경할 수 있습니다. 공연/전시의 포스터로 설정할 수도 있고, 본인의 갤러리에서 사진을 불러와 변경할 수도 있습니다.
        - 관람한 날짜를 선택해서 기록할 수 있습니다.
        - 한줄평과 리뷰 본문을 작성할 수 있습니다.
        - 확인 버튼을 누르면 작성한 리뷰가 저장됩니다.
    - 상단의 삭제 버튼을 누르면 작성한 리뷰가 삭제됩니다.

---

## <개발 후기>
