

Compile
-------

```
# すべてのパッケージを一括でコンパイルする
sbt
# root projectでcompileすることで、一括でコンパイルできる
compile

# service/dictionary(および依存パッケージ)のみをコンパイルする
project dictionary
compile
```

serviceを起動させる
----------------------

``````
sbt

# 全サービスルーティングを含んだサーバを起動させる
project dictionary
re-start
...
re-stop

# service/dictionaryのサーバのみを起動させる
project dictionary
re-start
...
re-stop
````


