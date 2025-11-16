# Twilight
2025 NE:XT CONTEST 팀 어스름 출품작  
당신의 독서 여정을 도와줄 AI 도서 추천 서비스 어스름


<a href="https://www.instagram.com/2025_nextcon/" target="_blank">
<img src="https://static.vecteezy.com/system/resources/previews/031/737/233/non_2x/instagram-social-media-logo-icons-instagram-icon-free-png.png" alt="instagram" width="40" />
</a>  

<a href="https://heady-eucalyptus-1e4.notion.site/2025-nextcon" target="_blank">
<img width="30" alt="notion" src="https://github.com/user-attachments/assets/dcf0e693-99d1-4904-8904-a82e49c7fbc7"/>
</a>


## Architecture
<img src="https://github.com/user-attachments/assets/3facea72-6996-4789-afd0-3e52027f6850" width="700"/>


## Documents
### AI
- see [twilight AI README](https://github.com/Sami9166/twilight-AI/blob/main/README.md)
### Backend
- see [twilight backend README](https://github.com/JokeBear777/twilight-backend)
## Features
### 개인 맞춤형 도서 추천 시스템

본 프로젝트의 핵심 기능인 개인 맞춤형 도서 추천 시스템은 기획 의도에 맞게 구현되었습니다.
기존의 단순한 장르 기반 추천 방식이 아닌, 자체적으로 구축한 의미 기반 태그 시스템을 활용합니다.
각 도서에는 1개의 장르 태그(문학, 역사, 사회, 경제 등)와 3개의 세부 태그(예: 감정의 잔상, 제도의 민낯)가 부여되어, 총 약 500개의 세부 태그로 세밀하게 분류됩니다.

### 다단계 태그 필터링

사용자가 일상적 혹은 감성적인 질문에 답변하면, 해당 답변은 사전에 정의된 태그와 매칭됩니다.
이후 서버는 다음과 같은 단계적 필터링을 통해 도서 후보군을 선별합니다:

- 1단계: 장르 태그 기준 필터링

- 2단계: 장르 태그 + 세부 태그 1개

- 3단계: 장르 태그 + 세부 태그 2개

- 위 단계를 거쳐도 5~15권을 확보하지 못할 시 전체 도서 중 일부를 무작위로 추출

이와 같은 점진적 필터링 구조는 정확성과 다양성을 동시에 확보하기 위한 전략으로,
사용자의 응답과 태그를 최대한 반영하면서도 AI 추천이 가능한 도서 후보군을 보장합니다.

### 백엔드와 메시지 큐 구조

필터링된 도서 데이터와 사용자 정보는 Redis Stream 기반 메시지 큐를 통해
비동기적으로 AI 서버에 전달됩니다.
백엔드는 생산자, AI 서버는 소비자 역할을 수행하며,
이 구조를 통해 처리 지연 없이 빠른 응답성과 확장성을 확보했습니다.
사용자 요청이 많아지더라도, 큐 기반의 비동기 설계를 통해 작업을 유연하게 분산할 수 있습니다.

### AI 추천 파이프라인

AI 서버는 전달받은 도서 데이터를 FAISS(Facebook AI Similarity Search)로 벡터화하고,
사용자의 답변과 매칭된 태그를 기반으로 쿼리 벡터를 생성합니다.
이후 코사인 유사도(Cosine Similarity)를 활용해 의미적으로 가장 유사한 도서를 검색하고, RAG(Retrieval-Augmented Generation)을 통해 LLM이 최종 추천을 진행합니다.

AI 서버는 LangChain과 Hugging Face Inference API를 직접 연결하는 custom wrapper를 구성하여 모델을 로컬에서 직접 실행하지 않아도 빠르고 효율적인 추론이 가능하도록 설계했습니다.
또한 새로운 도서 데이터 추가 시 모델 재학습 없이 DB만 갱신하면 되므로 유지보수 측면에서도 유리합니다.

### 독서 기록
독서 기록 기능은 추천받은 책을 저장하고, 읽은 과정을 기록하는 기능으로 기획되었습니다.
처음에는 사용자가 여러 관점에서 감상을 남길 수 있도록 질문을 제시하는 방식을 고려했지만,
이 방식이 오히려 사용자의 자유로운 감상에 영향을 줄 수 있다는 문제점을 발견했습니다.

이에 따라 팀은 논의를 거쳐 질문 기능을 제외하고, 기록 기능만 유지하기로 결정했습니다.
최종적으로 사용자는 읽은 책과 그에 대한 개인적인 기록을 다이어리 형태로 저장할 수 있으며,
이를 통해 자신의 독서 경험을 되돌아보고 다시 감상할 수 있는 구조로 설계되었습니다.

## Design & UX
프로젝트의 목표는 사용자에게 ‘자신만의 여정’을 제공하는 것이기에,
전체적으로 **편안하고 따뜻한 사용자 경험(UX)**을 중심으로 설계되었습니다.

웹사이트의 색상은 베이지 톤으로 통일하여
오래된 지도 같은 질감과 현대적인 감성을 함께 전달했습니다.
또한 쿠키런 폰트와 border-radius 속성을 적용해
둥근 인상과 시각적 안정감을 주었습니다.

기능적으로도 사용자의 편안함을 고려했습니다.
AI가 추천 결과를 생성하는 동안 책 관련 명언을 표시해
로딩 중 UX를 개선했고, 하단에는 내비게이터를 배치하여 손쉬운 페이지 이동을 지원했습니다.
홈 화면은 사이트 제목과 단일 버튼만으로 구성해
핵심 기능인 AI 책 추천을 즉시 이용할 수 있도록 했습니다.

짧은 개발 기간과 접근성을 고려하여 본 프로젝트는 모바일 웹 기반으로 개발되었습니다.
브라우저만으로 서비스를 이용할 수 있어 앱 설치 과정이 필요 없으며,
모바일 환경에 최적화된 UI로 언제 어디서든 서비스 접근이 가능합니다.
또한 웹 기반 특성상 PC 및 태블릿 환경과도 자연스럽게 호환되어
일관된 사용자 경험을 제공합니다.

## Open Sources
| 모듈명 | 사용한 기능 | 라이선스 |
|---------|--------------|-----------|
| httpx | 파이썬 HTTP 클라이언트 관리 | BSD-3-Clause License |
| LangChain | RAG 및 LLM을 통한 추천 시스템 구현 | MIT License |
| huggingface_hub | Embedding Model 및 HuggingFace Inference API 사용 | Apache-2.0 License |
| FastAPI | AI 서버 웹 프레임워크 구현 | MIT License |
| redis-py | 백엔드 서버로부터 받은 Redis 처리 | MIT License |
| uvicorn | FastAPI 프레임워크 서버 구현체 | BSD-3-Clause License |
| Spring Boot | 백엔드 서버 개발, REST API 및 MVC 아키텍처 구성 | Apache License 2.0 |
| Spring Data JPA | ORM 기반 DB 접근 | Apache License 2.0 |
| Hibernate | JPA 구현체 | LGPL 2.1 |
| Spring Security | 사용자 인증 및 세션 관리 | Apache License 2.0 |
| Lombok | 코드 축약 및 보일러플레이트 제거 | MIT License |
| MySQL | 도서, 사용자, 기록 등의 정형 데이터 저장 및 관리 | GPL (with FOSS exception) |
| MySQL Connector | MySQL과의 연동 | GPL v2 |
| Redis | 메시지 큐 기반 비동기 작업 처리 및 캐싱 | BSD-3-Clause License |
| Jackson | JSON 직렬화 / 역직렬화 | Apache License 2.0 |
| SLF4J | 로그 기록 | MIT / EPL License |
| Bootstrap | 반응형 UI 및 빠른 스타일링 구현 | MIT License |
| JQuery | DOM 조작 및 이벤트 처리 | MIT License |
| Font Awesome | 아이콘 사용 | CC BY 4.0 License |

