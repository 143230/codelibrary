# -*- coding: UTF-8 -*- 
import goslate
import datetime,time
import threadpool
import threading

mutex = threading.Lock()
count=0
cur=0
map={}
pool = threadpool.ThreadPool(800)
gs = goslate.Goslate()
def TranslateLine(sline,lang,index):
    global count,mutex
    global gs,cur
    count+=1
    target=''
    while True:
        try:
            after_tran=gs.translate(sline,lang)
            break
        except Exception,e:
            if mutex.acquire():
                print index,e
                mutex.release()
            time.sleep(3)
    after_tran=after_tran.encode("utf-8")
    target+=after_tran+"\t"+sline+"\n"
    if mutex.acquire():
        if index > cur:
            cur = index
            print index,"Yes"
        mutex.release()
    return target
def handle_result(request,result):
    global mutex
    if mutex.acquire():
        #print "id:",request.args[2]
        map[request.args[2]] = result
        mutex.release()
def TranslateFile(ss,lang):
    input = open(ss,'r')
    lines = input.readlines()
    i=0
    for line in lines:
        i+=1
        #print i
        line=line[:-1]
        req = threadpool.WorkRequest(TranslateLine,args=[line,lang,i],kwds={},callback=handle_result)
        pool.putRequest(req)
    input.close()


TranslateFile("jd_c_translate.words","en")
print "Waiting to stop"
pool.wait()
pool.stop()
output = open("jd_result",'w')
for i in range(1,10000):
    if map.has_key(i):
        output.write(map[i])
    else:break
print "Stop"

