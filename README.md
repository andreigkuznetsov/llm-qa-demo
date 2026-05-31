# LLM QA Demo (Java 21 + Gradle + Ollama + Qwen3 14B)

Демо-проект для тестирования LLM как вероятностной системы через Java 21, JUnit 5, Rest Assured, Jackson и AssertJ.

## Стек
- Java 21
- Gradle
- JUnit 5
- Rest Assured
- Jackson
- AssertJ
- Ollama
- Qwen3 14B (`qwen3:14b`)

## Что покрыто
- API contract и latency
- soft assertions
- strict JSON / structure validation
- faithfulness / grounding
- hallucination resistance
- direct / indirect injection и jailbreak
- stability / prompt variations
- golden dataset
- LLM-as-a-Judge
- clarity
- input validation / schema skew
- overly generic responses
- retrieval ranking metrics
- bias/fairness checks
- A/B model comparison scaffold
- classification metrics: confusion matrix, accuracy, precision, recall, F1, AUC-ROC
- local retrieval corpus with in-memory vector search

## Быстрый старт
### 1. Подними Ollama и модель
```bash
ollama pull qwen3:14b
```

### 2. Запусти тесты
Если Gradle установлен локально:
```bash
gradle test
```

Либо после генерации wrapper на своей машине:
```bash
gradle wrapper
./gradlew test
```

## Smoke и full suite
Тесты разделены через JUnit 5 tags:

- `smoke` — быстрые критичные проверки: API contract, structure validation, security, golden dataset.
- `full` — полный набор LLM QA проверок.

Запуск smoke:
```bash
./gradlew smokeTest
```

Запуск full:
```bash
./gradlew fullTest
```

Запуск всего набора:
```bash
./gradlew test
```

## Конфигурация
По умолчанию:
- base URL: `http://localhost:11434`
- model: `qwen3:14b`

Переопределение параметров:
```bash
./gradlew test   -Dollama.baseUrl=http://localhost:11434   -Dollama.model=qwen3:14b
```

> В коде используются системные свойства `ollama.baseUrl` и `ollama.model` в нижнем регистре. Используй именно их:

```bash
./gradlew test   -Dollama.baseUrl=http://localhost:11434   -Dollama.model=qwen3:14b
```

Пороги suite можно менять так:
```bash
./gradlew test   -Dllm.thresholds.api.maxResponseTimeMs=7000   -Dllm.thresholds.stability.maxUniqueResponses=4   -Dllm.thresholds.judge.minRelevance=3   -Dllm.thresholds.judge.minSafety=4
```

## Ресурсы
- `src/test/resources/golden-dataset.json` — golden cases
- `src/test/resources/thresholds.properties` — пороги для latency, judge и stability


## Дополнительные ML/AI QA улучшения

В проект добавлены production-oriented блоки, которые закрывают требования вакансий по ML/AI QA:

### Bias / Fairness
Класс: `BiasFairnessTest`

Проверяет, что одинаковые кандидаты с разными гендерными маркерами получают сопоставимую оценку, а объяснение не содержит стереотипных рассуждений.

### A/B model evaluation
Класс: `ModelComparisonTest`

Сравнивает две модели на одном evaluation set. По умолчанию отключён, чтобы тесты не падали, если в Ollama не загружена вторая модель.

Включение:
```bash
./gradlew fullTest -Dllm.ab.enabled=true -Dllm.ab.modelA=qwen3:14b -Dllm.ab.modelB=llama3.2
```

Перед запуском второй модели:
```bash
ollama pull llama3.2
```

### Classification metrics
Класс: `ClassificationMetricsTest`

Проверяет расчёт confusion matrix, accuracy, precision, recall, F1 и AUC-ROC на synthetic binary classification dataset.

### Local retrieval corpus
Класс: `RetrievalCorpusTest`

Использует локальный corpus `src/test/resources/retrieval-corpus.json`, in-memory retrieval и bag-of-words embeddings. Это уже ближе к real retrieval evaluation, чем полностью synthetic ranked list, но всё ещё без Qdrant/vector DB.

В main-коде также есть `OllamaEmbeddingService`, который можно использовать для перехода к настоящим embeddings через Ollama, например с моделью `nomic-embed-text`.

```bash
ollama pull nomic-embed-text
```

## GitHub Actions
В проект добавлен workflow:

```text
.github/workflows/llm-qa.yml
```

Он запускает:
- `smokeTest` на PR/push
- `fullTest` вручную и по расписанию

Важно: workflow устанавливает Ollama и скачивает `qwen3:14b`. Для реального CI это может быть тяжело по времени и ресурсам. В боевом проекте лучше использовать self-hosted runner с уже установленной моделью.

## Важный нюанс про wrapper
В этом контейнере не было установленного Gradle и не было доступа к сети, поэтому `gradle/wrapper/gradle-wrapper.jar` не сгенерирован.

Что сделать у себя один раз:
```bash
gradle wrapper
```

После этого можно использовать:
```bash
./gradlew test
```

## Что говорить о проекте на собеседовании

Этот проект можно описывать так:

> Я сделал демо-фреймворк для тестирования LLM как вероятностной системы, а не как обычного deterministic API. В нём есть несколько уровней проверок: API contract, latency, структурная валидация JSON через Jackson DTO, golden dataset, security/adversarial tests, проверка hallucination resistance, faithfulness/grounding, stability, LLM-as-a-Judge, fairness checks, A/B model comparison, classification metrics и локальный retrieval corpus. Для локального запуска используется Ollama + Qwen3 14B, а тестовый стек построен на Java 21, JUnit 5, Rest Assured, Jackson и AssertJ.

Можно дополнить:

> Я разделил suite на smoke и full: smoke подходит для быстрых проверок в PR, full — для регулярной оценки качества модели и промптов. Такой подход помогает контролировать не только техническую доступность API, но и качество, безопасность, fairness, регрессию поведения модели и базовые ML-метрики.

## Что можно развить дальше
- добавить Allure-отчёты;
- сохранять историю метрик между прогонами;
- подключить настоящий Qdrant/vector DB вместо in-memory retrieval;
- вынести prompts в отдельные файлы;
- добавить self-hosted CI runner с предзагруженной моделью.
