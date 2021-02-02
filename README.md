# Loopang
- Team Treasure 한이음 Project Loopang
- 루프스테이션이라는 음향장비를 안드로이드 앱으로 구현하였습니다. 
- 루프스테이션의 특징을 이어 받아, 녹음된 소리를 반복 시키고 그 위에 다른 반복되는 소리를 쌓는 음향 작업 방식을 지원합니다. 
- 만들어진 작품을 서로 공유하여 의견을 나눌 수 있고, 다른 사람의 작품을 내려받아 자신의 작업에 활용할 수 있습니다.

## 시연 영상
[![루팡유튜브썸네일](https://img.youtube.com/vi/Tu0eyBPZUZg/0.jpg)](https://www.youtube.com/watch?v=Tu0eyBPZUZg)

## 견본 이미지
![루팡견본이미지](https://meeta.io:3000/static/user/0pkoxcp9o8fd.png)

## 기능
- 모바일 디바이스 만으로 루프스테이션을 활용한 음악 창작 방식을 그대로 재현하여 즉흥적으로 반주를 창작할 수 있습니다.
- 기존 녹음된 소리 위에 새로운 소리를 녹음하여 반주를 만들 수 있습니다.
- 점점 쌓이며 녹음되는 음원소스들을 레이어라고 부르는데, 이펙터라는 특수한 효과 모듈을 사용하여 레이어의 톤을 조정할 수 있습니다.
- 창작된 반주를 바탕으로, 반주를 조작하거나 보컬을 추가하여 하나의 곡을 만들 수 있습니다.
- 녹음 시 화이트노이즈를 제거하여 음질을 개선할 수 있습니다.
- 녹음 시 메트로놈 기능을 지원합니다.
- 제스처 기반 UI, UX를 제공합니다.
- 자신의 음원소스를 타인과 공유할 수 있습니다.
- Feed에서 최신소스, Like top 5 리스트를 볼 수 있습니다

## 사용 스택
- Kotlin(안드로이드, 클라이언트)
- RxAndroid(클라이언트,UI)
- Kotlin coroutine library(클라이언트, UI, 비지니스로직)
- Figma (클라이언트, UI, 프로토타입제작)
- Retrofit(클라이언트, 통신)
- okHttp API(클라이언트, 통신)
- Docker(서버)
- Python(서버)
- Flask(서버)
- JWT(서버, 세션관리),
- PostgreSQL, SQLAlchemy(DB, ORM)
- GitHub, GitLab(버전관리)


