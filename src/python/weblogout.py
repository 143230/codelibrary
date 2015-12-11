# _*_ coding:utf-8 _*_

import urllib,httplib
import re

httpClient = None
try:
    headers = {'Host':'w.seu.edu.cn',
            'Connection':'keep-alive',
            'Accept':'application/json, text/javascript, */*; q=0.01',
            'Origin':'http://w.seu.edu.cn',
            'X-Requested-With':'XMLHttpRequest',
            'User-Agent':'Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.99 Safari/537.36',
            'Content-Type':'application/json; charset=UTF-8',
            'Referer':'http://w.seu.edu.cn/portal/index.html',
            'Accept-Encoding':'gzip, deflate',
            'Accept-Language':'zh-CN,zh;q=0.8',
            'Cookie':'iPlanetDirectoryPro=AQIC5wM2LY4Sfcz8oCqxqSj4ei38AR25ymeo2ay07YvUt5M%3D%40AAJTSQACMDE%3D%23'}
    httpClient = httplib.HTTPConnection('w.seu.edu.cn',80,timeout=10)
    httpClient.request('POST','/portal/logout.php',None,headers)
    response = httpClient.getresponse()
    print response.status,response.reason
    result = response.read()
    match = re.findall(r"{\"(?:error|success)\":\"(.*)\".*}",result)
    if len(match)>0:
        status = match[0]
        res_str=""
        for each in re.findall("\\u([0-9a-f]{4})",status):
            res_str+=unichr(int(each,16))
        print res_str
    else:
        print result
except Exception, e:
    print e
finally:
    if httpClient:
        httpClient.close()

