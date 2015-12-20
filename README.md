# locest

[![Build Status](https://travis-ci.org/morikuni/locest.svg?branch=master)](https://travis-ci.org/morikuni/locest)

テキストから位置情報を推定するAPI（作成中）

全3~4モジュールで構成される予定。
現在はareaモジュール, frequencyモジュールのみ完成。

## 準備

以下に依存します。
事前にインストールしておいてください。
AnsibleはVagrantから実行するだけなので必要ないかも。

- sbt
- scala
- Vagrant
- Ansible
- Python3

## areaモジュール

areaディレクトリ以下のエリア情報を管理するモジュール。

### 仮想マシン(PostgreSQLサーバ)の準備

```bash
cd path/to/locest/root/
cd setup/area/vagrant
vagrant up # VagrantfileのboxがParallels用なので好きなプロバイダのCentOS7に変更

#エリアデータの取得とデータベースへ登録
cd ../scripts
python3 download_area_data.py
scala create_sql.scala
scala exec_sql.scala #パスワードはpassword
```

### 実行

```bash
cd path/to/locest/root/
sbt "project area" "run 9000"
```

### API

- GET http://localhost:9000/areas/ids (全エリアIDを取得する)
- GET http://localhost:9000/areas/ids/coordinate/:lat/:lng (緯度経度がlat,lngの位置にあるエリアのIDを取得)
- GET http://localhost:9000/areas/:id (エリアIDがidのエリアの情報を取得する)

## frequencyモジュール

frequencyディレクトリ以下の頻度情報を管理するモジュール。

### 仮想マシン(PostgreSQLサーバ)の準備

```bash
cd path/to/locest/root/
cd setup/frequency/vagrant
vagrant up # VagrantfileのboxがParallels用なので好きなプロバイダのCentOS7に変更
```

### 実行

```bash
cd path/to/locest/root/
sbt "project frequency" "run 9001"
```

### API

- GET http://localhost:9001/frequencies/word/:id (単語IDがidの頻度情報を全て取得する)
- GET http://localhost:9001/count/all/ (全ての出現回数を和を取得する)
- GET http://localhost:9001/morphemes?q=:q (文字列qを形態素解析して、単語と出現回数のリストに変換する)
- POST http://localhost:9001/register/sentence?s=:s&lat=:lat&lng=:lng (文字列sが緯度経度lat, lngで出現したものとして頻度情報に加える)



