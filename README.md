
プロジェクト
-----------

| プロジェクト名      | sbtプロジェクト名          | 説明                                                          |
| ------------------- | -------------------------- | ------------------------------------------------------------- |
| root                | root                       | 結合テスト用　すべてのサービスパッケージを包括する            |
| services/dictionary | dictionaryService          | 辞書サービス                                                  |
| libs/core           | core                       | coreライブラリ                                                |

ディレクトリ構成
----------------

```
./
    services/
        dictionary/ : Dictionaryサービスのコード
    libs/
        core        : coreライブラリのコード
    src/            : rootプロジェクトのコード
```

Compile
-------

```
# すべてのパッケージを一括でコンパイルする
sbt
# root projectでcompileすることで、一括でコンパイルできる
compile

# service/dictionary(および依存パッケージ)のみをコンパイルする
project dictionaryService
compile
```

serviceを起動させる
----------------------

``````
sbt

# 全サービスルーティングを含んだサーバを起動させる
re-start
...
re-stop

# service/dictionaryのサーバのみを起動させる
project dictionaryService
re-start
...
re-stop
````

補足
--------

インタラクティブではなく、コンソールで実行したいばあいは、

```
sbt "project dictionaryService" compile
```
などのように、project指定可能
