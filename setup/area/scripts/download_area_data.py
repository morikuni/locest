#! /usr/bin/env python3

import urllib.parse
import urllib.request
import xml.etree.ElementTree as ET
import html.parser
import re
import os
import sys
import io
import zipfile
from functools import reduce

DIR_PATH = "./area_data/"
FILE_TYPE= 6 #世界測地系緯度経度・GML形式
PREFECTURES= [
    "北海道",
    "青森県",
    "秋田県",
    "岩手県",
    "山形県",
    "宮城県",
    "福島県",
    "新潟県",
    "群馬県",
    "栃木県",
    "茨城県",
    "千葉県",
    "埼玉県",
    "東京都",
    "神奈川県",
    "静岡県",
    "山梨県",
    "長野県",
    "富山県",
    "石川県",
    "福井県",
    "滋賀県",
    "岐阜県",
    "愛知県",
    "三重県",
    "奈良県",
    "和歌山県",
    "大阪府",
    "京都府",
    "兵庫県",
    "岡山県",
    "鳥取県",
    "島根県",
    "広島県",
    "山口県",
    "香川県",
    "徳島県",
    "愛媛県",
    "高知県",
    "福岡県",
    "佐賀県",
    "長崎県",
    "大分県",
    "宮崎県",
    "熊本県",
    "鹿児島県",
    "沖縄県"
]

parser = html.parser.HTMLParser()
def city_ids_of(prefecture):
    request = build_city_list_request(prefecture)

    with urllib.request.urlopen(request) as response:
        xml = ET.fromstring(parser.unescape(response.read().decode()))
        return [elem.attrib["value"] for elem in xml]

def build_city_list_request(prefecture):
    url = "http://e-stat.go.jp/SG2/eStatGIS/Service.asmx/GetDownloadStep3CityListTag"
    values = {
        "censusId": "A002005212010",
        "chiikiName": prefecture
    }
    return build_request(url, values)

def build_download_list_request(city_ids):
    url = "http://e-stat.go.jp/SG2/eStatGIS/Service.asmx/GetDownloadStep4ListTokeiTag"
    values = {
        "censusId": "A002005212010",
        "cityIds": ",".join(city_ids),
        "forHyou": "false",
        "statIds": "T000572"
    }
    return build_request(url, values)

def build_download_request(id, acode, ccode):
    url = "http://e-stat.go.jp/SG2/eStatGIS/downloadfile.ashx"
    values = {
        "pdf": 0,
        "id": id,
        "cmd": "D001",
        "type": FILE_TYPE,
        "tcode": "A002005212010",
        "acode": acode,
        "ccode": ccode
    }
    return build_request(url, values)

def build_request(url, values):
    data = urllib.parse.urlencode(values).encode("utf-8")
    return urllib.request.Request(url, data)


dodownload_re = re.compile(r"dodownload\(0,'(\d+)','{0}','A002005212010','(\w*)','(\d+)'\)".format(FILE_TYPE))
def extract_parameters(downlad_list_html):
    matches = dodownload_re.findall(downlad_list_html)
    return matches

def progressbar(percentage, width=80):
    width = max(10, width)
    inner = width-8
    formatter = "\r[{0:"+str(inner)+"}]{1:5.1f}%"
    sys.stdout.write(formatter.format("|"*int(inner*percentage), percentage*100))
    sys.stdout.flush()

if __name__ == "__main__":
    if not os.path.exists(DIR_PATH):
        os.makedirs(DIR_PATH)
    city_ids = reduce(lambda a, b: a+b, map(city_ids_of, PREFECTURES))

    print(len(city_ids), "cities found")

    with urllib.request.urlopen(build_download_list_request(city_ids)) as response:
        params = extract_parameters(parser.unescape(response.read().decode()))

    size = len(params)
    print("start downloading", size, "cities")
    progressbar(0)

    count = 0
    for id, acode, ccode in params:
        file = DIR_PATH + ccode + ".gml"
        if not os.path.exists(file):
            with urllib.request.urlopen(build_download_request(id, acode, ccode)) as response:
                zip = zipfile.ZipFile(io.BytesIO(response.read()))
                for filename in zip.namelist():
                    if ".gml" in filename:
                            with open(file, "wb") as f:
                                f.write(zip.read(filename))
        count += 1
        progressbar(++count/size)

    sys.stdout.write("\n")
