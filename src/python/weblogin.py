# _*_ coding:utf-8 _*_

import urllib,httplib
import re
import sys
import getpass
import tty,termios

httpClient = None

def readChar():
    fd = sys.stdin.fileno()
    old_settings = termios.tcgetattr(fd)
    try:
        tty.setraw(sys.stdin.fileno())
        ch = sys.stdin.read(1)
    finally:
        termios.tcsetattr(fd,termios.TCSADRAIN,old_settings)
    return ch

def readString(hint,passwd=True):
    print hint,
    chars=[]
    while True:
        newchar = readChar() 
        if newchar in '\r\n' or newchar in '\n':
            print ''
            break
        elif newchar in '\b':
            if len(chars)>0:
                del chars[-1]
                sys.stdout.write('\b')
                sys.stdout.write(' ')
                sys.stdout.write('\b')
        else:
            chars.append(newchar)
            if passwd:
                sys.stdout.write('*')
            else:
                sys.stdout.write(newchar)
    return "".join(chars)

try:
    username = sys.argv[1] if len(sys.argv)>1 else None
    password = sys.argv[2] if len(sys.argv)>2 else None
    if username==None:
        username = readString("Please Input User Account : ",False)
    if password == None:
        password = readString("Please Input User Password: ")
    print "Logging in Account: "+username
    params = urllib.urlencode({'username':username,'password':password})
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
    httpClient.request('POST','/portal/login.php',params,headers)
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

