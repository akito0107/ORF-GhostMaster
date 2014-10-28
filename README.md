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

<pre><code> $ sbt </code></pre>

と入力する。最初はかなり時間がかかる。待っていると、

<pre><code> > </code></pre>
  
というプロンプトが表示されるので

<pre><code> > compile </code></pre>
  
もしくは

<pre><code> > clean compile </code></pre>
  
と入力する。


実行方法
--------
### infinispanの設定
infinispanがないと動きません。
http://infinispan.org/download/
から6.0.2.Finalの *server* をダウンロードして来てください。
zipを解凍すると、

<pre><code>
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
</code></pre>

こんな感じのディレクトリ構成になっているはず。
standalone/configuration以下にslackで伝えたコンフィグファイルを設置してください。
設置したらinfinispanのルートディレクトリにもどって、

<pre><code> % bin/standalone.sh -c ghost_standalone.xml </code></pre>
  
と入力してください（場合によってはsudoが必要）これでinfinispanが起動します。

### 実行方法
プロジェクトルートで

<pre><code>
% sbt
> compile
</code></pre>
  
と入力してください。
まずはWorkerを起動します。

<pre><code>
>run Worker
</code></pre>
  
これでworkerが起動します。別のシェルを開いて、

<pre><code>
% sbt
> run Gateway
</code></pre>
  
でメインのGatewayが起動します。sampleAppの実行はMain.scalaからはコメントアウトしていますが、元にもどして動作を確認してみてください。
  
