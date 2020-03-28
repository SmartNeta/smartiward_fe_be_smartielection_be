<?xml version="1.0"?> 
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">
    <style>
        .bold_header {
        text-align:center;
        background: #ffd966;
        width:100%;
        }
		
        table {
        border-collapse: collapse;
        }
		
        th{
        font-size: 9px;
        text-align :center;
        padding: 3px 1px;
        border: 1px solid #fff;
        }
	        	
        td {
        border: 1px solid black;
        padding: 5px 1px;
        font-size: 9px;
        text-align :center;
        border-bottom:0;
        border-left:0;
        }
				
        .blank_row
        {
        height: 10px !important;
        background-color: #FFFFFF;
        }
		
        .foo{
        width:20px;
        height:20px;
        border:1px solid black !important;
        }
        #firstTab td {
        border: 0;
        }
        .reportTitle{
        font-size: 24px;
        font-weight: bold;
        text-align: center;
        margin-bottom: 5px;
        padding: 3px;
        }
        
    </style>
    <body>
        <#assign lastUpdated = .now>
        <div class="bold_header" style="padding-bottom: 10px;">  
            <div class="reportTitle">Complaints Summary Report</div>
            <div style="font-size:16px; font-weight:bold; padding-bottom: 10px;">Ward# ${wardNumber}</div>
            <span style="font-size: 10px;"> Last updated: ${lastUpdated?string.medium} </span>
        </div>
        <div style="float:right;padding-bottom: 20px; padding-right: 25px;">
            <table id="firstTab" style="border:0; border-collapse: separate;">
                <tbody>
                    <tr>
                        <td></td>
                        <td>
                            <b>Status Description</b>
                        </td>
                    </tr>
                    <tr>
                        <td class="foo" style="background-color:#ff0000"></td>
                        <td style="text-align: left;"> &lt; 25% total complaints resolved</td>
                    </tr>
                    <tr>
                        <td class="foo" style="background-color:#ffc000"></td>
                        <td style="text-align: left;">26% - 50% total complaints resolved</td>
                    </tr>
                    <tr>
                        <td class="foo" style="background-color:#ffff00"></td>
                        <td style="text-align: left;">51% - 75% total complaints resolved</td>
                    </tr>
                    <tr>
                        <td class="foo" style="background-color:#00b050"></td>
                        <td style="text-align: left;">76% - 100% total complaints resolved</td>
                    </tr>				
                </tbody>
            </table>
			
        </div>
        <div style="padding: 1px;">
            <table width="100%">
                <thead>
                    <#if totalYears??>
                        <tr>
                            <th colspan="3">&nbsp;</th>
                            <#list totalYears as year>
                                <th colspan="${(totalPartiesCode?size + 3)}" style="background: #595959; color:#fff; padding: 5px;">Election Results  ${year}</th>
                                <th>&nbsp;&nbsp;</th>
                            </#list>
                            <th colspan="8" style="background: #000000; color:#fff;">Status of complaints received till date</th>
                        </tr>
                    </#if>
                    <tr     style="background-color: #ffc000; color:#fff;height: 50px !important;">
                        <th style="background-color: #ffffff; color:#000; width:15px;">Sr. No</th>
                        <th style="background-color: #595959; ">Booth #</th>
                        <th style="background-color: #ffffff; border: 0 ; color:#fff"></th>
                        <#list totalYears as year>
                            <th style="background-color: #d9d9d9; color:#000;">Total voters</th>
                            <th style="background-color: #d9d9d9; color:#000;">Total Polled</th>
                            <th style="background-color: #d9d9d9; color:#000;">% Polled</th>
                            <#list totalPartiesCode as partyName>
                                <th style="background-color: #d9d9d9; color:#000;">${partyName}</th>
                            </#list>
                            <th style="background-color: #ffffff; border: 0 ; color:#f2f2f2"></th>
                        </#list>
                        <th style="background-color: #a6a6a6; border-left: 0; width:40px; ">Total Compliants</th>
                        <th style="background-color: #000000; width:45px;">% resolved</th>
                        <th style="width:42px;">Resolved</th>   
                        <th style="width:42px;">No Status</th>
                        <th style="width:42px;">In progress</th>
                        <th style="width:58px;">Under Review</th>
                        <th style="width:58px;">Out Of Scope</th>
                        <th style="width:42px;">Ignore</th>
                    </tr>
                </thead>
                <tbody>
                    <tr class="blank_row">
                        <td style="border: 0 "></td>
                    </tr>
                    <#if overallResult??>
                    <tr style="font-weight: bold;background-color: #fff2cc;height: 50px !important;">
                        <td colspan="3" style="font-size: 14px !important;border: 1px solid #fff;border-right: 0;">Overall Result</td>
                        <#list totalYears as year>
                            <#assign overAll_Voters = ('overAll_Voters_' + year)>
                            <#assign overAll_Polled = ('overAll_Polled_' + year)>
                            <td style="border-left: 1px solid #000000; background-color: #d9d9d9; ">
                                <#if overAllMap[overAll_Voters] &gt; 999>
                                    ${(overAllMap[overAll_Voters] / 1000)?string["0.##"]}K
                                <#else>
                                    ${overAllMap[overAll_Voters]}
                                </#if>
                            </td>
                            <td style="background-color: #d9d9d9; ">
                                <#if overAllMap[overAll_Polled] &gt; 999>
                                    ${(overAllMap[overAll_Polled] / 1000)?string["0.##"]}K
                                <#else>
                                    ${overAllMap[overAll_Polled]}
                                </#if>                                
                            </td>
                            <td style="background-color: #d9d9d9; "><#if overAllMap[overAll_Polled] &gt; 0 && overAllMap[overAll_Polled] &gt; 0 >  ${((overAllMap[overAll_Polled]) * 100 / overAllMap[overAll_Voters])?string("0")}% <#else>0 </#if></td>
                            <#list totalPartiesCode as partyName>
                                <#assign overAll_Party = ('overAll_Party_' + year + '_' + partyName)>
                                <td style="background-color: #d9d9d9; ">
                                    <#if (overAllMap[overAll_Party])??>
                                        <#if (overAllMap[overAll_Party])?is_number && (overAllMap[overAll_Party] &gt; 999)>
                                            ${((overAllMap[overAll_Party]/1000))?string["0.##"]}K
                                        <#else>
                                            ${overAllMap[overAll_Party]}
                                        </#if>
                                    <#else>
                                        0
                                    </#if>
                                </td>
                            </#list>
                            <td style="border: 0;background-color: #fff"></td>
                        </#list>
                        <td style="border-left: 1px solid #000000; border-bottom: 1px solid #000000; background-color:#f2f2f2"><#if (overallResult[2])??> ${overallResult[2]} </#if></td>
                        <#assign avg_percentage =  (overallResult[3])>
                        <#if avg_percentage &gt; 75>
                            <td style="border-bottom: 1px solid black; background-color: #00b050;">${avg_percentage?string["0.##"]}%</td> 
                        <#elseif avg_percentage &gt; 50>
                            <td style="border-bottom: 1px solid black; background-color: #ffff00;">${avg_percentage?string["0.##"]}%</td> 
                        <#elseif avg_percentage &gt; 25>
                            <td style="border-bottom: 1px solid black; background-color: #ffc000;">${avg_percentage?string["0.##"]}%</td> 
                        <#else>
                            <td style="border-bottom: 1px solid black; background-color: #ff0000;">${avg_percentage?string["0.##"]}%</td> 
                        </#if>
                        
                        <#assign ind = 0>
                        <#list overallResult as overallResultObject>  
                            <#if ind &gt; 3 && ind != (overallResult?size - 1)>
                                <td style="border: 1px solid #000000;border-left: 0;">${overallResultObject}</td>
                            </#if>
                            <#assign ind = ind + 1>
                        </#list>
                        <td style="border-right: 1px solid black;border-bottom: 1px solid #000000;">${overallResult[overallResult?size - 1]}</td>
                    </tr>
                    </#if>
                    <tr class="blank_row">
                        <td style="border: 0 !important;"></td>
                    </tr>
                    <#if reportData??>
                        <#assign count = 1>
                        <#list reportData as result>
                            <#if count != (reportData?size)>
                                <tr     style="background-color:#fff2cc; ">
                                    <td style="background-color:#ffffff; border: 0 ;">${count}</td>
                                    <td style="background-color:#d9d9d9; border: 0 ;"><#if (result[1])??> ${result[1]} <#else>-</#if></td>
                                    <td style="background-color:#ffffff; border: 0 ;">&nbsp;&nbsp;&nbsp;</td>
                                    <#list totalYears as year>
                                        <#assign key = ('key_' + year + '_' + count)>
                                            <#list mappp[key] as myValue>
                                                <td style="background-color: #d9d9d9; ">
                                                    <#if (myValue)??>
                                                        <#if (myValue)?is_number && (myValue &gt; 999)>
                                                            ${((myValue/1000))?string["0.##"]}K
                                                        <#else>
                                                            ${myValue}
                                                        </#if>
                                                    <#else> 
                                                        0
                                                    </#if>
                                                </td>
                                            </#list>
                                        <td style="border: 0;background-color: #fff"></td>
                                    </#list>
                                    
                                    <td style="background-color:#f2f2f2; border-left:1px solid black;">${result[2]}</td>                                   
                                    <#assign percentage = (result[3])>
                                    <#if percentage &gt; 75>
                                        <td style="background-color: #00b050; border-right: 0;">${percentage?string["0.##"]}%</td> 
                                    <#elseif percentage &gt; 50>
                                        <td style="background-color: #ffff00; border-right: 0;">${percentage?string["0.##"]}%</td> 
                                    <#elseif percentage &gt; 25>
                                        <td style="background-color: #ffc000; border-right: 0; ">${percentage?string["0.##"]}%</td> 
                                    <#else>
                                        <td style="background-color: #ff0000; border-right: 0;">${percentage?string["0.##"]}%</td> 
                                    </#if>
                                    
                                    <#assign ind = 0>
                                       <#list result as resultData>
                                            <#if ind &gt; 3 && ind != (result?size - 1)>
                                                <td style="border-left: 1px solid black;border-right: 0;">${result[ind]}</td>
                                            </#if>
                                            <#assign ind = ind + 1>
                                        </#list>
                                    <td style="border-left:1px solid black; border-right: 1px solid black;">${result[result?size - 1]}</td>
                                </tr>
                                <#assign count = count + 1>
                            <#else>
                                <tr style="background-color:#fff2cc">
                                    <td style="background-color:#fff;border: 0 ;">${count}</td>
                                    <td style="background-color:#d9d9d9;border: 0 ;"><#if (result[1])??> ${result[1]} <#else>-</#if></td>
                                    <td style="background-color:#fff;border: 0 ;">&nbsp;&nbsp;&nbsp;</td>
                                    <#list totalYears as year>
                                        <#assign key = ('key_' + year + '_' + count)>
                                            <#list mappp[key] as myValue>
                                                <td style="border-bottom: 1px solid black; background-color: #d9d9d9; ">
                                                    <#if (myValue)??>
                                                        <#if (myValue)?is_number && (myValue &gt; 999)>
                                                            ${((myValue/1000))?string["0.##"]}K
                                                        <#else>
                                                            ${myValue}
                                                        </#if>
                                                    <#else> 
                                                        0
                                                    </#if>
                                                </td>
                                            </#list>
                                        <td style="border-bottom: 1px solid black; background-color: #ffffff; border: 0;"></td>
                                    </#list>
                                    
                                    <td style="border-left: 1px solid black; border-bottom: 1px solid black; background-color:#f2f2f2;">${result[2]}</td>
                                    <#assign percentage = (result[3])>
                                    <#if percentage &gt; 75>
                                        <td style="border-bottom: 1px solid black; background-color: #00b050; border-right: 0;">${percentage?string["0.##"]}%</td> 
                                    <#elseif percentage &gt; 50>
                                        <td style="border-bottom: 1px solid black; background-color: #ffff00; border-right: 0;">${percentage?string["0.##"]}%</td> 
                                    <#elseif percentage &gt; 25>
                                        <td style="border-bottom: 1px solid black; background-color: #ffc000; border-right: 0;">${percentage?string["0.##"]}%</td> 
                                    <#else>
                                        <td style="border-bottom: 1px solid black; background-color: #ff0000; border-right: 0;">${percentage?string["0.##"]}%</td> 
                                    </#if>
                                    <#assign ind = 0>
                                    <#list result as resultData>
                                        <#if ind &gt; 3 && ind != (result?size - 1)>
                                            <td style="border-bottom: 1px solid black;border-right: 0;border-left: 1px solid black;">${result[ind]}</td>
                                        </#if>
                                        <#assign ind = ind + 1>
                                    </#list>
                                    <td style="border-left: 1px solid black; border-bottom: 1px solid black;">${result[result?size - 1]}</td>
                                </tr>
                            </#if>
                        </#list>
                        <#else>
                        <tr style="background-color:#fff;font-weight:bold;">
                            <td colspan="5" style="border: 0;">Records Not Found.</td>
                        </tr>
                    </#if>          
                </tbody>
            </table>
        </div>
    </body>
</html>