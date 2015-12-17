# locest

[![Build Status](https://travis-ci.org/morikuni/locest.svg)](https://travis-ci.org/morikuni/locest)

テキストから位置情報を推定するAPI（作成中）

全3~4モジュールで構成される予定。
現在はareaモジュールのみ完成。

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

### VMの準備

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

- http://localhost:9000/areas/ids (全エリアID取得)
- http://localhost:9000/areas/ids/coordinate/:lat/:lng (緯度経度がlat,lngの位置にあるエリアのIDを取得)
- http://localhost:9000/areas/:id (エリアIDがidのエリアの情報を取得する)



