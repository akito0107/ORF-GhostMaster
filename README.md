ORF-GhostMaster
===============

Main cluster
MEMSYSのORFプロジェクト用のレポジトリです。
メインの計算クラスタのプロジェクトです。

環境
------
JDK 1.6 (1.7や1.8ではダメ)
SBT 0.13.6
infinispan-server-6.0.2.Final
それ意外の諸々は勝手にSBTがとってきます。


ビルド方法
-------
### IntelliJ
このプロジェクトをインポートして、SBT syncを行うと勝手にビルドします

### コマンドラインから
プロジェクトルートで
  $ sbt
と入力する。最初はかなり時間がかかる。待っていると、
  > 
というプロンプトが表示されるので
  > compile 
もしくは
  > clean compile
と入力する。


実行方法
--------
### infinispanの設定
infinispanがないと動きません。
http://infinispan.org/download/
から6.0.2.Finalの *server* をダウンロードして来てください。
zipを解凍すると、
  .
  ├── bin
  │   └── init.d
  ├── client
  │   ├── hotrod
  │   └── rest
  ├── docs
  │   ├── examples
  │   └── schema
  ├── modules
  │   └── system
  ├── rest
  └── standalone
      ├── configuration
      ├── data
      ├── lib
      ├── log
      └── tmp
こんな感じのディレクトリ構成になっているはず。
standalone/configuration以下にslackで伝えたコンフィグファイルを設置してください。
設置したらinfinispanのルートディレクトリにもどって、
  % bin/standalone.sh -c ghost_standalone.xml
と入力してください（場合によってはsudoが必要）これでinfinispanが起動します。

### 実行方法
プロジェクトルートで
  % sbt
  > compile
と入力してください。
まずはWorkerを起動します。
  >run Worker
これでworkerが起動します。別のシェルを開いて、
  % sbt
  > run Gateway
でメインのGatewayが起動します。sampleAppの実行はMain.scalaからはコメントアウトしていますが、元にもどして動作を確認してみてください。
  
