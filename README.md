![joonpago-playstore.png](https://prod-files-secure.s3.us-west-2.amazonaws.com/23df9efd-7793-4306-8450-377de89e7a75/94247c08-cb36-42a3-a3e6-0aa6f2b9282c/joonpago-playstore.png)


# 💡 Topic

- 급한 상황에서 스마트폰 카메라를 이용해서 실시간 번역 서비스를 지원하는 앱

# ⭐️ Key Function

- 텍스트 번역 및 음성 번역(TTS) 서비스
- 카메라를 이용한 실시간 번역
- 카메라 촬영을 이용한 전체 번역
- 한영 번역 이외 MLkit에 등록된 59개 언어 번역 기능

# 🛠 Skills

`Kotlin`, `JetPackCompose`, `CameraX`, `MLkit`, `MediaStore`

# 🧑🏻‍💻 Team

- 안드로이드 개발자 1명

# 🤚🏻 Part

- **안드로이드 앱 개발**
- UI 디자인 구상 및 구현

# 🤔 도전해 본 것

- **`MLkit`**을 이용해 번역 기능 구현 해보기
    - **MLkit Text recognition**을 사용해 실시간 텍스트 인식
    - **Translator** 를 사용해 인식된 텍스트를 번역  ****
- **`CameraX` 라이브러리를 활용하여 카메라 프리뷰, 이미지 캡처, 이미지 분석 기능을 구현.**
    - **CameraSelector**를 통해 전면 및 후면 **카메라를 선택하고 동적으로 전환** 가능하게 구현.
    - **ImageAnalysis**를 활용해 ML Kit의 **텍스트 인식 모델과 연동, 실시간 분석 기능** 구현.
    - **DisplayManager**와 CameraX의 설정을 활용해 화면 회전 및 **디스플레이 크기에 따라 카메라 뷰를 조정.**
- **`MediaStore`**를 이용한 사진 저장 프로세스 구성
    - **ContentValues**를 사용하여 이미지의 이름, MIME 타입, 저장 경로 등 **메타데이터 설정**
    - **MediaStore.Images.Media.EXTERNAL_CONTENT_URI** 를 사용하여 **외부 저장소에 저장**

---

# 📷 Screenshot

![Screenshot_20250110_092806_JOONPAGO.jpg](https://prod-files-secure.s3.us-west-2.amazonaws.com/23df9efd-7793-4306-8450-377de89e7a75/6782c4aa-d344-4147-93ef-d0182be7c55c/Screenshot_20250110_092806_JOONPAGO.jpg)

![Screenshot_20250110_092823_JOONPAGO.jpg](https://prod-files-secure.s3.us-west-2.amazonaws.com/23df9efd-7793-4306-8450-377de89e7a75/b1701141-e8a4-4791-bad9-a0663e8ce03a/Screenshot_20250110_092823_JOONPAGO.jpg)

![Screenshot_20250110_093021_JOONPAGO.jpg](https://prod-files-secure.s3.us-west-2.amazonaws.com/23df9efd-7793-4306-8450-377de89e7a75/40c5e342-9101-4429-a6a8-115536a3ee67/Screenshot_20250110_093021_JOONPAGO.jpg)

![Screenshot_20250110_093016_JOONPAGO.jpg](https://prod-files-secure.s3.us-west-2.amazonaws.com/23df9efd-7793-4306-8450-377de89e7a75/6f245e0a-2060-4fa0-98aa-9d95ed55f0fd/Screenshot_20250110_093016_JOONPAGO.jpg)

![Screenshot_20250110_092924_JOONPAGO.jpg](https://prod-files-secure.s3.us-west-2.amazonaws.com/23df9efd-7793-4306-8450-377de89e7a75/f52512d7-8e6a-482f-96b8-1bcb4cd9679e/Screenshot_20250110_092924_JOONPAGO.jpg)

![Screenshot_20250110_092949_JOONPAGO.jpg](https://prod-files-secure.s3.us-west-2.amazonaws.com/23df9efd-7793-4306-8450-377de89e7a75/7f17d10b-f364-4019-8409-a39cda93835d/Screenshot_20250110_092949_JOONPAGO.jpg)

---

# 📝 회고

### **Learned(배운 점)**

- compose를 사용하는데 있어 익숙해졌다.
- CAMERAX, MLkit, 등 다양한 스킬들을 공부하고 구현 하며 공부했다.
- 기능 구현에 있어 기능별로 클래스를 만들어서 호출하는 방식으로 구현해야 효율적이다.
- 검색능력이 올랐다.
- ai, 및 다른 사람의 코드를 보고 내 프로젝트에 어떤 것들을 적용하면 좋은지 조금은 알게 되었다.

### **Lacked(아쉬운 점)**

- 구현 하고 싶었던 기능들을 다 구현하지 못했다.
- compose navigation을 사용해보지 못해서 아쉬웠다.
- 개발을 하며 중간중간 다른 기능 구현으로 넘어가곤 했는데. 다음엔 필수 기능을 먼저 구현하고 다른 기능을 추가하는 방식으로 해야겠다.
    - 번역기 앱이면 기본적인 번역 기능을 완성하고 거기에 살을 붙여나가는 식으로 해야겠다.
- 번역기 앱이면 기본적인 번역 기능을 완성하고 거기에 살을 붙여나가는 식으로 해야겠다.
- 다음 프로젝트에는 기획단계에서 간단한 디자인 정도는 기획 해놔야겠다.
