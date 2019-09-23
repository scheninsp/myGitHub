%An implementation for sliding window covariance calculation
%INPUT: 
% - sig : nTime * nChan
% - timeBin : window length
% - timeStep : stride
%OUTPUT:
% - Rvalue : nCorr * timeBin
% , nCorr = nChan * (nChan-1)/2


% sig1 = linspace(1,100,100);
% sig2 = [linspace(1,50,50),linspace(50,1,50)];
% sig3 = [linspace(50,1,50),linspace(1,50,50)];
% sig = [sig1.',sig2.',sig3.'];
% timeBin = 10;
% timeStep = 8;

fs = 64;
sig = randn(fs*180,64);  %180s
timeBin = round(0.4*fs);   %0.4s win
timeStep = round(0.1*fs);  %0.1s step

nChan = size(sig,2);
nTime = size(sig,1);

if_use_matrix = 1;   %use matrix caculation or loop calculation

nCorr = (1+nChan-1)*(nChan-1)/2;
if (nCorr*nTp*timeBin > 10e10)
    disp('Matrix is too large to use parallel computation, use loop instead')
    if_use_matrix = 0;
end

if if_use_matrix   %matrix form
    %reform sig
    sdata = sig;
    proc_time_start2=tic;
    timePoint=1:timeStep:size(sdata,1)-timeBin+1;  
    nTp = length(timePoint);
    xMat1 = zeros(nChan*nTp,timeBin);
    for iChan=1:nChan
        for it = 1:timeBin
            xMat1((iChan-1)*nTp+1:iChan*nTp,it) = sdata(timePoint+it-1,iChan);
        end
    end
    %extend sdata for correlation permutation

    sMat1 = zeros(nCorr*nTp,timeBin);
    sMat2 = zeros(nCorr*nTp,timeBin);

    istart = 1; iend = (nChan-1)*nTp;
    for iChan=1:nChan
        sMat1(istart:iend,:) = repmat(xMat1(((iChan-1)*nTp+1 : iChan*nTp),:),nChan-iChan,1);
        sMat2(istart:iend,:) = xMat1((iChan*nTp+1 : nChan*nTp),:);
        istart = iend +1;
        iend = iend + (nChan-(iChan+1))*nTp;  %allocate next space 
    end

    sMat1 = sMat1 - repmat(mean(sMat1,2),1,timeBin);
    sMat2 = sMat2 - repmat(mean(sMat2,2),1,timeBin); 
    sMat3 = sum(sMat1 .* sMat2,2);
    sMat4 = sqrt(sum(sMat1.^2,2) .* sum(sMat2.^2,2) );

    Rvalue = sMat3./sMat4;
    Rvalue = reshape(Rvalue,nTp,nCorr);
    Rvalue = Rvalue.';
    proc_time_cost=toc(proc_time_start2)/60;
    disp(['matrix form rvalue cost : ',num2str(proc_time_cost)])

else  %loop form
    
    sdata = sig.';
    proc_time_start2=tic;
    timePoint=1:timeStep:size(sdata,2)-timeBin+1;   
    Rvalue=zeros((nChan*nChan-nChan)/2,length(timePoint));   
    short_time_power = zeros(nChan,length(timePoint));
    for i3=1:length(timePoint)
        n=0;
        for i1=1:nChan
            short_time_power(i1,i3) = mean(sdata(i1,timePoint(i3):timePoint(i3)+timeBin-1));
            for i2=(i1+1):nChan
                n=n+1;        
                [R P]=corrcoef(sdata(i1,timePoint(i3):timePoint(i3)+timeBin-1),sdata(i2,timePoint(i3):timePoint(i3)+timeBin-1));
                Rvalue(n,i3)=R(1,2);  %1-2, 1-3 ...1-n, 2-3, 2-4 ...
            end
        end
    end
    proc_time_cost=toc(proc_time_start2)/60;
    disp(['loop form rvalue cost : ',num2str(proc_time_cost)])
end