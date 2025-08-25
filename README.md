# 電卓アプリ
### このアプリについて
この電卓アプリは、GUIを採用し、PCのキーボード入力に加え、画面上のボタンをクリックすることで数式を直感的に入力できるように設計しました。 

### このアプリの機能
- 入力された文字列を計算式として計算する機能(足し算・引き算・割り算・掛け算・括弧・小数等が使用可能)
- 入力された計算式を履歴に保存する機能
- 履歴から過去に入力された計算式を取得し、再計算する機能(最大20件まで)
- 従来の電卓と同じように計算する機能(左から右に順番に計算)
- 四則計算のルールに従って計算する機能(掛け算・割り算から先に計算)
- 括弧を使用すると、その中を先に計算する機能
- コンソール入力を使用した、CUIで電卓を使用する機能(履歴機能は使用できません)

### 開発環境
- Eclipse(Pleiades All in One) 最新版
- JDK 21.0.8
- Maven(Pleiadesに付属)

### 実行環境
- Windows 11 23H2 または 24H2
- Java SE 21<br>
  ※Windows以外の環境では正しく動作しない可能性があります。

### このアプリの使い方
「Releases」から、zipファイルをダウンロード・展開し、中に入っている"Calculator.jar"をダブルクリックすると起動します。<br>
※Java実行環境(JRE)が必要です。<br>
CUI電卓機能を使用する場合は、`java -jar Calculator.jar nogui`を"Calculator.jar"を保存したフォルダで実行します。
<details>
    <summary>スクリプトモードを使用するには</summary>

  スクリプトモードを使用するには、CUI電卓機能の起動時にJVM引数`-D`を使用して、システムプロパティ`calc.CUICalculator.scriptMode`を`true`に設定する必要があります。<br>
  詳細は[Javadoc](https://hotaru-6316.github.io/Calculator/calc/CUICalculator.html#scriptMode)を参照してください。
</details>

### ビルド
  ※Eclipseを使用した手順です。他のIDEの場合は、下のMavenを使用した手順を参照してください。
1. このリポジトリをクローンし、プロジェクトとしてインポートします。
2. インポートしたプロジェクトを選択した状態で、「Maven プロジェクトの更新」を実行します。「pom.xml からプロジェクト構成を更新」「プロジェクトをクリーン」の2つにチェックが入っていることを確認してから実行してください。
3. 終了後、「起動構成」から、「Maven ビルド」→「Calculator Build」を実行します。
4. ほとんどのテストは操作を必要としませんが、一部のテストは手動での操作を必要とします。その場合は、指示に従って操作してください。
5. Mavenビルドが終了すると、targetフォルダの中にCalculator.jarが生成されます。
<br>

   ※Mavenを使用した手順です。Eclipseを使用する場合は、上のEclipseを使用した手順がお勧めです。
1. このリポジトリをクローンします。以後、クローンしたリポジトリのディレクトリの場所を、`C:\repo`として表現します。この部分は、実際にクローンしたリポジトリのディレクトリに置き換えて、実行してください。
2. POMファイル`C:\repo\pom.xml`で、Mavenゴール`clean package`を実行してください。
3. ほとんどのテストは操作を必要としませんが、一部のテストは手動での操作を必要とします。その場合は、指示に従って操作してください。
4. Mavenビルドが終了すると、targetフォルダの中にCalculator.jarが生成されます。

### Javadoc
Javadocドキュメントは[こちら](https://hotaru-6316.github.io/Calculator/)から使用できます。
ただし、このドキュメントにテストソースのJavadocは含まれていません。

### ライセンス
このプロジェクトは、MITライセンスを使用しています。詳細については、[LICENSE](https://github.com/hotaru-6316/Calculator/blob/main/LICENSE)ファイルを参照してください。

ただし、「Releases」に公開されているバイナリには、[H2 Database engine](https://h2database.com/)の改変されていないバイナリが含まれています。
これはデュアルライセンスであり、MPL 2.0 (Mozilla Public License) または EPL 1.0 (Eclipse Public License) の下で利用可能です。
詳細は[H2 DatabaseのLicenseページ](https://h2database.com/html/license.html)をご確認ください。