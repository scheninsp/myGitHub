from sys import getsizeof

import pyedflib
import numpy as np

currentfile = 'sjk_acq1_cont.edf'

'''
#read all file
print("processing %s" % currentfile)
f = pyedflib.EdfReader(currentfile)
print("edfsignals: %i" % f.signals_in_file)
print("file duration: %i seconds" % f.file_duration)

n = f.signals_in_file
signal_labels = f.getSignalLabels()
sig = np.zeros((n, f.getNSamples()[0]))
for i in np.arange(n):
    sig[i, :] = f.readSignal(i)
    
sig_fs = []    
for i in np.arange(n):
    sig_fs.append(f.getSampleFrequency(i))

#calculate memory cost
tmp=getsizeof(sig)
tmp=tmp/1024/1024
print("label: %s" % signal_labels)
print ("sig: %.2f MB" % tmp)    #the memory cost is nearly 4 times of edf file, why?
print("fs: %s" % sig_fs)
'''

#------------------------------------------------------

#compress signal using zlib  : approximately 75% compression rate
#zlib is using modified LZW compression ?
import zlib
with open("sjk_acq1_cont.edf", "rb") as in_file:
    compressed = zlib.compress(in_file.read(), 9)

with open(currentfile+'_cb_zlib', "wb") as out_file:
    out_file.write(compressed)

'''
#------------------------------------------------------
#compress signal using gzip
def get_hash(data):
    return hashlib.md5(data).hexdigest()

import gzip
import os
import hashlib

data = open("sjk_acq1_cont.edf", 'rb').read()
cksum = get_hash(data)

print 'Level  Size        Checksum'
print '-----  ----------  ---------------------------------'
print 'data   %10d  %s' % (len(data), cksum)

for i in xrange(9,10):
    filename = 'compress-level-%s.gz' % i
    output = gzip.open(filename, 'wb', compresslevel=i)
    try:
        output.write(data)
    finally:
        output.close()
    size = os.stat(filename).st_size
    cksum = get_hash(open(filename, 'rb').read())
    print '%5d  %10d  %s' % (i, size, cksum)
#-----------------------------------------------------------------
'''


