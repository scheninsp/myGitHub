clear;clc;close all;

base_tax = [1:10];
base_production = [1:10];
%base_manpower = [1:10];
base_manpower = 3;
market_price = [2:0.5:5];

% base_tax = 3;
% base_production = 3;
% base_manpower = 2;
% market_price = 3;

religious_penalty = 0;
culture_penalty = 0;
has_courthouse = 0;
has_statehouse = 0;

nTax = length(base_tax);
nProduction = length(base_production);
%nManpower = length(base_manpower);
nManpower = 1;
nMarketPrice = length(market_price);

nTest =3;
sum_of_tp_perTest = zeros(nTest, nTax, nProduction, nMarketPrice);
governing_capacity_perTest = zeros(nTest, nTax, nProduction);
trade_income_perTest = zeros(nTest, nTax, nProduction, nMarketPrice);

for iTest = 1:nTest
    
    if (iTest == 1)   
        is_state = 1;
        is_company = 0;
    elseif (iTest == 2)
        is_state = 0;
        is_company = 1;
    elseif (iTest == 3)
        is_state = 0;
        is_company = 0;
    elseif (iTest == 4)
        is_state = 0;
        is_company = 0;
    else
        error('test number exceeds configured');
    end
    
    if is_state>0
        local_autonomy = 10;
    else
        local_autonomy = 90;
    end

    if (is_company>0)
        local_autonomy_for_production = local_autonomy/2;
    else
        local_autonomy_for_production = local_autonomy;
    end

    %production modifiers
    event_market_modifers = 0;
    flat_value_modifiers = 0 %1 + 0.3;   %manufactory + companybuilding
    production_efficiency_modifiers = 4 %+ 50 + 50;  %workshop + companybuilding

    if( religious_penalty > 0 && is_company <= 0 )
        religious_penalty_for_production = -15;
    else
        religious_penalty_for_production = 0;
    end
    goods_produced_modifiers= 0 + religious_penalty_for_production;

    %tax modifiers
    local_tax_income = 0;

    if(religious_penalty > 0 && is_company <= 0 )
        religious_penalty_for_tax = -15;
    else
        religious_penalty_for_tax = 0;
    end
    if(culture_penalty > 0 && is_company <= 0  )
        culture_penalty_for_tax = -33;
    else
        culture_penalty_for_tax = 0;
    end
    tax_income_efficiency = 100 + religious_penalty_for_tax + culture_penalty_for_tax + 5 %+ 40;   %church
    
    %building modifiers
    building_modifier_for_capacity = 0;
    building_flat_debuff = 0;
    year_to_reclaim = 100;
    if(has_courthouse > 0)
        building_modifier_for_capacity = building_modifier_for_capacity + 25;
        building_flat_debuff = building_flat_debuff + 85/ (year_to_reclaim*12);
    end
    if(has_statehouse > 0)
        building_modifier_for_capacity = building_modifier_for_capacity + 20;
        building_flat_debuff = building_flat_debuff + 375/ (year_to_reclaim*12);
    end
    
    %production 
    goods_produced = (base_production*0.2 +flat_value_modifiers)*(1+goods_produced_modifiers/100);
    goods_produced = floor2(goods_produced);
    goods_produced = maxwith001(goods_produced);

    trade_value = zeros(length(base_production), length(market_price));
    for i = 1:length(base_production)
        for j = 1:length(market_price)
            trade_value(i,j) = market_price(j)*(1+event_market_modifers/100)*goods_produced(i);
        end
    end

    production_income_monthly = (1+production_efficiency_modifiers/100)*(1-local_autonomy_for_production/100).*trade_value / 12;
    production_income_monthly = floor2(production_income_monthly);
    production_income_monthly = maxwith001(production_income_monthly);

    %tax
    tax_income_monthly = (base_tax+local_tax_income)/12*(1-local_autonomy/100)*tax_income_efficiency/100;
    tax_income_monthly = floor2(tax_income_monthly);
    tax_income_monthly = maxwith001(tax_income_monthly);

    %state maintanence
    if (is_state>0 )
        state_maintanence = zeros(nTax, nProduction);

        distance_modifer_for_maintanence = 1.3;
        if(culture_penalty > 0)
            culture_modifier_for_maintanence = 0.25;
        else
            culture_modifier_for_maintanence = 0;
        end
        
        for i=1:nTax
            for j=1:nProduction
            state_maintanence(i,j) = 0.007*(base_tax(i) + base_production(j) + base_manpower)*(1+culture_modifier_for_maintanence) * distance_modifer_for_maintanence;
            end
        end
    else
        state_maintanence = zeros(nTax, nProduction);
    end
   
    
    %total
    sum_of_tp = zeros(nTax, nProduction, nMarketPrice);
    for i=1:nTax
        for j=1:nProduction
            for k = 1:nMarketPrice
                sum_of_tp(i,j,k) = tax_income_monthly(i) + production_income_monthly(j,k) - state_maintanence(i,j) + building_flat_debuff;
            end
        end
    end
    
    %temporary check result
    theMarketPrice = 3;

    [xmesh, ymesh] = meshgrid(base_production, base_tax);
    figure;
    sum_of_tp_plot = squeeze(sum_of_tp(:,:,theMarketPrice));
    mesh(xmesh, ymesh, sum_of_tp_plot);
    xlabel('base production');
    ylabel('base tax');
    colorbar
    title(['sum of tp at test ', num2str(iTest)])

    sum_of_tp_perTest(iTest,:,:,:) = sum_of_tp;
    
    if(is_company >0)
        governing_capacity_modifer = 0.5;
    elseif (is_state > 0)
        governing_capacity_modifer = 1;  
    else
       governing_capacity_modifer = 0.25 ;  
    end
    

    governing_capacity_modifer = max(governing_capacity_modifer - building_modifier_for_capacity/100 , 0); 
    
    cost_of_governing_capacity = zeros(nTax, nProduction);
    for i=1:nTax
        for j=1:nProduction
            cost_of_governing_capacity(i,j) = (base_tax(i) + base_production(j) + base_manpower) * governing_capacity_modifer ;  
            cost_of_governing_capacity(i,j)  = max(floor(cost_of_governing_capacity(i,j)),1);
            
            %restrictor
            if (cost_of_governing_capacity(i,j) < eps)
                cost_of_governing_capacity(i,j) = 0.001;
            end
        end
    end
    
    %temporary check result
    figure;
    plot(tax_income_monthly ./ cost_of_governing_capacity(:,4).')
    box off;
    xlabel('base tax');
    ylabel('tax income / capacity');
    
    figure;
    plot(production_income_monthly(:,theMarketPrice).' ./ cost_of_governing_capacity(4,:))
%     set(gca,'YLim',[0.1,0.2])
    box off;
    xlabel('base production');
    ylabel('production income / capacity');
    
    governing_capacity_perTest(iTest, :,:) = cost_of_governing_capacity;
    
    %trade
    if( is_company > 0 )
        trade_power_modifier = 2.3;
    elseif(is_state > 0)
        trade_power_modifier = 1.3;
    else
        trade_power_modifer = 1.3 * 0.55;
    end
    
    trade_power = zeros(nTax, nProduction);
    for i=1:nTax
        for j=1:nProduction
            trade_power(i,j) = 0.2 * (base_tax(i) + base_production(j) + base_manpower) * trade_power_modifier;
        end
    end
    transferring_trade_power = 200;
    trade_steering = 1.12;
    trade_power_proportion = trade_steering * trade_power  ./ (transferring_trade_power + trade_power); 
    
    end_point_incoming = zeros(nTax, nProduction, nMarketPrice);
    for i = 1:nTax
        for j=1:nProduction
            for k=1:nMarketPrice
                end_point_incoming(i,j,k) = 1.075^4 * 0.93^4 * (0.8 + trade_power_proportion(i,j)) * trade_value(j,k);  %pass 4 nodes with 7.5% increase per node
            end
        end
    end
    trade_income = end_point_incoming * 1.2;
    
    %temporary check result
    [xmesh, ymesh] = meshgrid(base_production, base_tax);
    figure;
    sum_of_tp_plot = squeeze(trade_income(:,:,theMarketPrice));
    mesh(xmesh, ymesh, sum_of_tp_plot);
    xlabel('base production');
    ylabel('base tax');
    colorbar
    title(['trade income at test ', num2str(iTest)])
    
    trade_income_perTest(iTest,:,:,:) = trade_income;
    
end

theMarketPrice = 3;

comp1 =1;
comp2 =2;
if (comp1 == 1 && comp2 == 2)
    base_sum_of_tp = squeeze(sum_of_tp_perTest(3,:,:,:));
    base_trade_income = squeeze(trade_income_perTest(3,:,:,:));
    base_governing_capacity = squeeze(governing_capacity_perTest(3,:,:));
elseif(comp1 ==2 && comp2 == 3)
    base_sum_of_tp = zeros(nTax,nProduction,nMarketPrice);
    base_trade_income = zeros(nTax,nProduction,nMarketPrice);
    base_governing_capacity = zeros(nTax,nProduction);
else
    error('unsupported comparison')
end

[xmesh, ymesh] = meshgrid(base_production, base_tax);
sum_of_tp_plot = squeeze(sum_of_tp_perTest(comp1,:,:,theMarketPrice) - sum_of_tp_perTest(comp2,:,:,theMarketPrice));
figure;
mesh(xmesh, ymesh, sum_of_tp_plot);
xlabel('base production');
ylabel('base tax');
colorbar;
title('Tax Production Income Increasement')

[xmesh, ymesh] = meshgrid(base_production, base_tax);
sum_of_tp_plot = squeeze(trade_income_perTest(comp1,:,:,theMarketPrice) - trade_income_perTest(comp2,:,:,theMarketPrice));
figure;
mesh(xmesh, ymesh, sum_of_tp_plot);
xlabel('base production');
ylabel('base tax');
colorbar;
title('Trade Income Increasement')

[xmesh, ymesh] = meshgrid(market_price, base_production);
plotmat = zeros(nProduction,nMarketPrice);
for i = 1:nProduction
    for j = 1:nMarketPrice
        plotmat(i,j) = trade_income_perTest(comp1,i,i,j) - trade_income_perTest(comp2,i,i,j);
    end
end
figure;
mesh(xmesh, ymesh, plotmat);
xlabel('market price');
ylabel('base production');
colorbar;
title('Trade Income Increasement 2')


[xmesh, ymesh] = meshgrid(base_production, base_tax);
sum_of_tp_plot = squeeze(trade_income_perTest(comp1,:,:,theMarketPrice) - trade_income_perTest(comp2,:,:,theMarketPrice)) + ...
                    squeeze(sum_of_tp_perTest(comp1,:,:,theMarketPrice) - sum_of_tp_perTest(comp2,:,:,theMarketPrice));
figure;
mesh(xmesh, ymesh, sum_of_tp_plot);
xlabel('base production');
ylabel('base tax');
colorbar;
title('Total Income Increasement')

[xmesh, ymesh] = meshgrid(market_price, base_production);
plotmat = zeros(nProduction,nMarketPrice);
for i = 1:nProduction
    for j = 1:nMarketPrice
        plotmat(i,j) = sum_of_tp_perTest(comp1,i,i,j) - sum_of_tp_perTest(comp2,i,i,j) + trade_income_perTest(comp1,i,i,j) - trade_income_perTest(comp2,i,i,j);
    end
end
figure;
mesh(xmesh, ymesh, plotmat);
xlabel('market price');
ylabel('base production');
colorbar;
title('Total Income Increasement 2')


[xmesh, ymesh] = meshgrid(base_production, base_tax);
plotmat =(squeeze(sum_of_tp_perTest(comp1,:,:,theMarketPrice)) - squeeze(base_sum_of_tp(:,:,theMarketPrice))) ./ ( squeeze(governing_capacity_perTest(comp1,:,:)) - base_governing_capacity) -  ...
            (squeeze(sum_of_tp_perTest(comp2,:,:,theMarketPrice)) - squeeze(base_sum_of_tp(:,:,theMarketPrice))) ./ (squeeze(governing_capacity_perTest(comp2,:,:)) - base_governing_capacity);
figure;
mesh(xmesh, ymesh, plotmat);
xlabel('base production');
ylabel('base tax');
colorbar;
title('Marginal TaxProduction Income to Capacity')


[xmesh, ymesh] = meshgrid(market_price,base_production);
plotmat = zeros(nProduction,nMarketPrice);
for i = 1:nProduction
    for j = 1:nMarketPrice
        
        plotmat(i,j) =(sum_of_tp_perTest(comp1,i,i,j) - base_sum_of_tp(i,i,j)) ./ ( governing_capacity_perTest(comp1,i,i) - base_governing_capacity(i,i)) -  ...
            (sum_of_tp_perTest(comp2,i,i,j) - base_sum_of_tp(i,i,j)) ./ (governing_capacity_perTest(comp2,i,i) - base_governing_capacity(i,i));

    end
end
figure;
mesh(xmesh, ymesh, plotmat);
xlabel('market price');
ylabel('base production');
colorbar;
title('Marginal TaxProduction Income to Capacity 2')


[xmesh, ymesh] = meshgrid(base_production, base_tax);
plotmat =(squeeze(trade_income_perTest(comp1,:,:,theMarketPrice)) - squeeze(base_trade_income(:,:,theMarketPrice))) ./ ( squeeze(governing_capacity_perTest(comp1,:,:)) - base_governing_capacity) -  ...
            (squeeze(trade_income_perTest(comp2,:,:,theMarketPrice)) - squeeze(base_trade_income(:,:,theMarketPrice))) ./ (squeeze(governing_capacity_perTest(comp2,:,:)) - base_governing_capacity);
figure;
mesh(xmesh, ymesh, plotmat);
xlabel('base production');
ylabel('base tax');
colorbar;
title('Marginal Trade Income to Capacity')


[xmesh, ymesh] = meshgrid(market_price,base_production);
plotmat = zeros(nProduction,nMarketPrice);
for i = 1:nProduction
    for j = 1:nMarketPrice
        
        plotmat(i,j) =(trade_income_perTest(comp1,i,i,j) - base_trade_income(i,i,j)) ./ ( governing_capacity_perTest(comp1,i,i) - base_governing_capacity(i,i)) -  ...
            (trade_income_perTest(comp2,i,i,j) - base_trade_income(i,i,j)) ./ (governing_capacity_perTest(comp2,i,i) - base_governing_capacity(i,i));
        
    end
end
figure;
mesh(xmesh, ymesh, plotmat);
xlabel('market price');
ylabel('base production');
colorbar;
title('Marginal Trade Income to Capacity 2')

[xmesh, ymesh] = meshgrid(market_price,base_production);
plotmat = zeros(nProduction,nMarketPrice);
for i = 1:nProduction
    for j = 1:nMarketPrice
        
        plotmat(i,j) =(trade_income_perTest(comp1,i,i,j) - base_trade_income(i,i,j) + sum_of_tp_perTest(comp1,i,i,j) - base_sum_of_tp(i,i,j)) ./ ( governing_capacity_perTest(comp1,i,i) - base_governing_capacity(i,i)) -  ...
            (trade_income_perTest(comp2,i,i,j) - base_trade_income(i,i,j) + sum_of_tp_perTest(comp1,i,i,j) - base_sum_of_tp(i,i,j)) ./ (governing_capacity_perTest(comp2,i,i) - base_governing_capacity(i,i));

    end
end
figure;
mesh(xmesh, ymesh, plotmat);
xlabel('market price');
ylabel('base production');
colorbar;
title('Marginal Total Income to Capacity 2')
