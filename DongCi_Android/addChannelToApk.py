import sys
import zipfile
import os

apkName=sys.argv[1]
channelCode=sys.argv[2]
src_channel_file = 'channel.txt'
logfile = 'log.txt'
f = open(src_channel_file, 'w')
f.close()


zipped = zipfile.ZipFile(apkName, 'a', zipfile.ZIP_DEFLATED)
hasExistsChannel = False
for file in zipped.namelist():
    if file.startswith("META-INF/dccc_"):
        hasExistsChannel = True
        print 'hello--write'
        break

file_object = open(logfile, 'w')
file_object.write("hello")
file_object.close( )
print 'hello'+str(hasExistsChannel)
if not hasExistsChannel:
    channel_file = "/META-INF/dccc_{channel}".format(channel=channelCode)
    zipped.write(src_channel_file, channel_file)
    with open(logfile,'w') as fileobject: 
        fileobject.write("exit-->"+str(os.path.exists(channel_file)))
    print "write-end"
zipped.close()