
'''
#test matplotlib 
print 'hello world';

import matplotlib.pyplot as plt
plt.plot([1,2,3])
plt.ylabel('some numbers')
plt.show()
'''

#function to resize pic
def save_resized_pic(plotmatrix,picname,clim):
    import numpy as np
    
    import scipy.ndimage
    import scipy.misc
    
    final_plot = np.flipud(plotmatrix)
    defined_resolution = (600,4000)
    original_resolution = plotmatrix.shape
    #print('%d,%d' % original_resolution)
    
    interp_x = float(defined_resolution[1])/float(original_resolution[1])
    interp_y = float(defined_resolution[0])/float(original_resolution[0])
    print('resize ratio for %s: \n x : %.2f, y : %.2f' % (picname,interp_x,interp_y))
    
    final_plot_new = scipy.ndimage.interpolation.zoom(final_plot,(interp_y,interp_x))
    
    scipy.misc.toimage(final_plot_new,cmin=clim[0],cmax=clim[1]).save('%s.jpg' % picname)


def processor_tfplot():
    
    from sys import getsizeof

    import pyedflib
    import matplotlib.pyplot as plt
    from scipy import signal
    import numpy as np
    
    currentfile = 'sjk_acq1_cont.edf'
    
    theChannel = 'POL DC01';
    #theChannel = 'POL E1';
    
    #read file
    print("processing %s" % currentfile)
    f = pyedflib.EdfReader(currentfile)
    print("edfsignals: %i" % f.signals_in_file)
    print("file duration: %i seconds" % f.file_duration)
    
    tmp=getsizeof(f)
    tmp=tmp/1024
    print ("file pointer: %.2f KB" % tmp)
    
    #read signal
    f_label=f.getSignalLabels()
    theChannel_num=f_label.index(theChannel)
    
    sig = f.readSignal(theChannel_num)
    sig_fs = f.getSampleFrequency(theChannel_num)
    
    data_range_remain = (1,10)
    sig = sig[int(data_range_remain[0]*sig_fs+1):int(data_range_remain[1]*sig_fs)]    #use only 10s for test
    
    tmp=getsizeof(sig)
    tmp=tmp/1024
    print("label: %s" % f.getLabel(theChannel_num))
    print ("sig: %.2f KB" % tmp)
    print("fs: %d" % sig_fs)
    
    '''
    #draw signal
    plt.plot(sig[0:20000])
    plt.ylabel('sig')
    plt.show()
    '''
    
    '''
    #draw signal using picture
    ylim = (int(min(sig)),int(max(sig)))
    print('min max ylim : %s %s' % ylim)
    min_spacing = (ylim[1]-ylim[0])/1000

    print('min_spacing of sig : %.2f' % min_spacing)

    ylim_num = int((ylim[1]-ylim[0])/min_spacing)
    xlim_num = len(sig)
    
    print((xlim_num,ylim_num))
    
    sig_pic = np.ones([ylim_num,xlim_num],dtype=int)
    
    line_width=10
    for i in range(line_width,len(sig)-line_width) :
        pic_indx_y = int((sig[i] - ylim[0])/min_spacing)
        if pic_indx_y > ylim_num-1 :
            pic_indx_y = ylim_num-1
        #sig_pic[pic_indx_y][i] = 0
        for j in range(-line_width,line_width):
            if (pic_indx_y+j) < (ylim_num-1) & (pic_indx_y+j) > 0:
                sig_pic[pic_indx_y+j][i-line_width:i+line_width] = 0
        
    clim_sig = (0,1)
    save_resized_pic(sig_pic,'sig',clim_sig)
    '''
    
    
    #compress signal
    
    #time-frequency analysis
    '''
    #test scipy cwt
    t = np.linspace(-1, 1, 200, endpoint=False)
    sig  = np.cos(2 * np.pi * 7 * t) + signal.gausspulse(t - 0.4, fc=2)
    widths = np.arange(1, 31)
    cwtmatr = signal.cwt(sig, signal.ricker, widths)
    plt.imshow(cwtmatr, extent=[-1, 1, 1, 31], cmap='PRGn', aspect='auto',
               vmax=abs(cwtmatr).max(), vmin=-abs(cwtmatr).max())
    plt.show()
    '''
    
    foi = np.arange(10,201,10)
    #The fundamental frequency of this wavelet in Hz is given by f = 2*s*w*r / M
    # where r is the sampling rate.
    wavelet_length_t = 0.2
    wavelet_width = 7 
    scales = wavelet_length_t / (2*wavelet_width) * foi
    
    wavelet_coeff = np.zeros([len(scales), len(sig)], dtype=complex)
    wavelet_energy = np.zeros([len(scales), len(sig)],dtype=float)
    
    for ind, scale in enumerate(scales):
    
        wavelet_data = signal.morlet(wavelet_length_t*sig_fs,wavelet_width,scale)
        
        ''' 
        #plot wavelets
        plt.plot(wavelet_data)
        plt.xlabel('time (10^-3 sec)')
        plt.ylabel('amplitude')
        plt.title('Morlet Wavelet for scale='+str(scale)+'\nwidth='+str(wavelet_length_t*sig_fs))
        plt.show()
        '''
        
        z = signal.convolve(sig, wavelet_data, mode='same')
    
        i = 0
        for complexVal in z:
            wavelet_coeff[ind][i] = complex(complexVal.real, complexVal.imag)         
            i+=1
         
        wavelet_energy[ind] = abs(wavelet_coeff[ind])
        wavelet_energy[ind] = wavelet_energy[ind]-wavelet_energy[ind].mean()
        wavelet_energy[ind]=wavelet_energy[ind]/wavelet_energy[ind].std()
    
    
    #plotmatrix = abs(wavelet_coeff)
    plotmatrix=wavelet_energy
    #plotrange_max=abs(plotmatrix).max()
    #plotrange_min=-abs(plotmatrix).max()
    
    
    '''
    #using matplotlib to visualize
    plotrange_max=5
    plotrange_min=-5
    print(plotrange_max)
    
    gci = plt.imshow(np.flipud(plotmatrix), extent=[0, len(sig),1,scales.size], cmap='jet', aspect='auto',
               vmax=plotrange_max, vmin=plotrange_min)
    ax = plt.gca()
    
    timetick_indx = np.arange(1,len(plotmatrix[1]),sig_fs)
    
    timeticklabels=[]
    for i in range(len(timetick_indx)) :
        #print((timetick_indx[i]-1)/sig_fs+data_range_remain[0])
        tmp = ((timetick_indx[i]-1)/sig_fs)+data_range_remain[0]
        timeticklabels.append('%.1f' % tmp)
    
    ax.set_xticks(timetick_indx)
    ax.set_xticklabels(timeticklabels)    #FOR TEST ONLY
    
    freqtick_indx = np.linspace(1,len(plotmatrix),num=5,dtype=int)
    print("plotmatrix size: %i" % len(plotmatrix))
    
    freqticklabels=[]
    for i in range(len(freqtick_indx)) :
        freqticklabels.append('%d' % foi[freqtick_indx[i]-1])
    
    ax.set_yticks(freqtick_indx)
    ax.set_yticklabels(freqticklabels)    
    
    plt.title(f.getLabel(theChannel_num))
          
    #plt.show()
    
    plt.savefig('tfplot')
    '''
    
    clim_tfplot = (-5,5)
    save_resized_pic(plotmatrix,'tfplot',clim_tfplot)
    

if __name__ == '__main__':
    processor_tfplot()
    