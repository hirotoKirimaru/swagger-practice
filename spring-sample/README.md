# library-sample (Spring Boot + Spring Batch)

OpenAPI 定義の図書館アプリ (`reference/index.yaml`) を題材にした Spring Boot サンプルです。

## スタック

| カテゴリ | ライブラリ | 用途 |
|---|---|---|
| ベース | Spring Boot 3.5.0 / Java 21 | アプリ基盤 |
| Web | spring-boot-starter-web | REST API |
| 永続化 | spring-boot-starter-data-jpa + H2 | エンティティ管理 |
| バッチ | spring-boot-starter-batch | CSV → DB 取り込み Job |
| バリデーション | spring-boot-starter-validation | Bean Validation (`@Valid`) |
| 監視 | spring-boot-starter-actuator | health / metrics |
| テスト | spring-boot-starter-test / spring-batch-test | JUnit5 / MockMvc / Job テスト |

## ディレクトリ

```
src/main/java/com/example/library/
├── LibraryApplication.java
├── batch/           # Spring Batch Job 定義
├── domain/          # JPA エンティティ
├── repository/      # Spring Data JPA
├── service/         # ドメインサービス
└── web/             # REST Controller + DTO + 例外ハンドラ
```

## 動かし方

```bash
cd spring-sample
./mvnw spring-boot:run           # 起動
./mvnw test                      # テスト
```

### REST API

| Method | Path | 説明 |
|---|---|---|
| GET | `/api/v1/books` | 一覧取得 |
| GET | `/api/v1/books/{isbn}` | 単体取得 |
| PUT | `/api/v1/books/{isbn}` | 登録 |
| POST | `/api/v1/books/{isbn}/actions` | 借用 / 返却 (`type: BORROW \| RETURN`) |
| POST | `/api/v1/batch/book-import` | Spring Batch Job キック |
| GET | `/actuator/health` | ヘルスチェック |

### Spring Batch サンプル

`classpath:data/books.csv` を読み取り、JPA 経由で `books` テーブルへ挿入する Job (`bookImportJob`)。

- **Reader**: `FlatFileItemReader` (CSV)
- **Processor**: `BookItemProcessor` (既存 ISBN は `null` を返してスキップ)
- **Writer**: `JpaItemWriter`
- **チャンクサイズ**: 100
- **耐障害性**: `IllegalArgumentException` を 10 件までスキップ

起動時の自動実行は `spring.batch.job.enabled=false` で抑止しており、
REST 経由 (`POST /api/v1/batch/book-import`) または `BookImportJobLauncher#launch()` で起動します。

## 設計メモ

- ドメインオブジェクト (`Book`) に振る舞い (`borrow` / `returnBook`) を寄せ、Service は薬剤師的にトランザクション境界を持つ役割に留めています。
- `Book#version` (`@Version`) で楽観ロックを有効化。
- 例外マッピングは `GlobalExceptionHandler` で 404/409/400 に変換。
- OpenAPI 側 (`reference/`) と整合する URL (`/api/v1/books`) を採用。
