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
        font-size: 11px;
        text-align :center;
        padding: 3px 0.5px;
        border: 1px solid #fff;
        }

        td {
        border: 1px solid black;
        padding: 5px 0.5px;
        font-size: 10px;
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
            <div class="reportTitle">Surveyed Data</div>
            <div class="reportTitle">Parliamentary Constituency Report</div>
            <div style="font-size:18px; font-weight:bold;padding-bottom: 10px;">District# ${districtName}</div>
            <span style="font-size: 12px; height: 15px;"> Last updated: ${lastUpdated?string.medium} </span>
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
                        <td style="text-align: left;"> &lt; 25% voters visited</td>
                    </tr>
                    <tr>
                        <td class="foo" style="background-color:#ffc000"></td>
                        <td style="text-align: left;">26% - 50% voters visited</td>
                    </tr>
                    <tr>
                        <td class="foo" style="background-color:#ffff00"></td>
                        <td style="text-align: left;">51% - 75% voters visited</td>
                    </tr>
                    <tr>
                        <td class="foo" style="background-color:#00b050"></td>
                        <td style="text-align: left;">76% - 100% voters visited</td>
                    </tr>				
                </tbody>
            </table>
			
        </div>
        <div style="padding: 1px;">
            <table width="100%">
                <thead>
                    <#if totalYears??>
                        <tr>
                        <th colspan="4">&nbsp;</th>
                        <#list totalYears as year>
                            <th colspan="${(totalPartiesCode?size + 3)}" style="background: #595959; color:#fff; padding: 5px;">${year}</th>
                            <th>&nbsp;&nbsp;</th>
                        </#list>
                        </tr>
                    </#if>
                    <tr style="background: #ffc000;color:#fff;height: 50px !important;">
                        <th style="background: #fff;color:#000;">Sr. No</th>
                        <th style="background-color: #595959; word-wrap: break-word;">A.C. Number#</th>
                        <th style="background-color: #595959; ">A.C. Name#</th>
                        <th style="background-color: #ffffff; border: 0; color:#fff">&nbsp;&nbsp;&nbsp;</th>
                      
                        <#list totalYears as year>
                            <th style="background-color: #d9d9d9; color:#000;">Total voters</th>
                            <th style="background-color: #d9d9d9; color:#000;">Total Polled</th>
                            <th style="background-color: #d9d9d9; color:#000;">% Polled</th>
                            <#list totalPartiesCode as partyName>
                                <th style="background-color: #d9d9d9; color:#000;">${partyName}</th>
                            </#list>
                            <th style="background-color: #ffffff; border: 0 ; color:#f2f2f2"></th>
                        </#list>  
                        <th style="background-color: #a6a6a6; border-left: 0;">Total Houses</th>
                        <th style="background-color: #a6a6a6; ">Total Voters</th>
                        <th style="background-color: #a6a6a6; ">Total Booths</th>                       
                        <th style="background-color: #000000; " colspan="4" >
                            Booth Status
                            <p>Shows the completion status of booths canvassed</p>
                        </th>
                        <th style="background-color: #ffffff; border: 0;color:#f2f2f2"></th>
                        <th style="width:40px;">
                            Voters Visited
                        </th>
                        <th style="width:40px;">
                            Voters Responded
                        </th>
                        
                        <#list partyNames as party>
                            <th style="width:35px;">${party}</th>
                        </#list>
                        
                        <th>Not disclosed</th>
                    </tr>
                </thead>
                <tbody>
                    <tr class="blank_row">
                        <td style="border: 0 "></td>
                    </tr>
                    <#if overallResult??>
                        <tr style="font-weight: bold; background-color: #fff2cc;height: 50px !important;">
                            <td style="font-size: 18px !important; border: 1px solid #fff;border-right: 0;" colspan="4">Overall Result</td>
                            <#list totalYears as year>
                                <#assign overAll_Voters = ('overAll_Voters_' + year)>
                                <#assign overAll_Polled = ('overAll_Polled_' + year)>
                                <td style="background-color: #d9d9d9; ">
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
                            <td style="background-color: #f2f2f2; border-bottom: 1px solid #000000; border-left: 1px solid #000000; "><#if (overallResult[0])??> ${overallResult[0]} </#if></td>
                            <td style="background-color: #f2f2f2; border-bottom: 1px solid #000000;"><#if (overallResult[1])??> ${overallResult[1]} </#if></td>
                            <td style="background-color: #f2f2f2; border-bottom: 1px solid #000000;"><#if (overallResult[2])??> ${overallResult[2]} </#if></td>
                            <td style="background-color: #00b050; border-bottom: 1px solid #000000;"><#if (overallResult[3])??> ${overallResult[3]} </#if></td>
                            <td style="background-color: #ffff00; border-bottom: 1px solid #000000;"><#if (overallResult[4])??> ${overallResult[4]} </#if></td>
                            <td style="background-color: #ffc000; border-bottom: 1px solid #000000;"><#if (overallResult[5])??> ${overallResult[5]} </#if></td>
                            <td style="background-color: #ff0000; border-bottom: 1px solid #000000;"><#if (overallResult[6])??> ${overallResult[6]} </#if></td>
                            <td style="background-color: #ffffff; border: 0 ;">&nbsp;&nbsp;&nbsp;</td>
                            <td style="border-left: 1px solid #000000;border-bottom: 1px solid #000000;"><#if (overallResult[7])??> ${overallResult[7]} </#if></td>
                                <#assign ind = 0>
                                <#list overallResult as overallResultObject>  
                                    <#if ind &gt; 7 && ind != (overallResult?size - 1)>
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
                                <tr     style="background-color: #fff2cc; ">
                                    <td style="background-color: #ffffff; border: 0 ;">${count}</td>
                                    <td style="background-color: #d9d9d9; border: 0 ;"><#if (result[0])??> ${result[0]} <#else> - </#if></td>
                                    <td style="background-color: #d9d9d9; border: 0 ;">${result[1]}</td>
                                    <td style="background-color: #ffffff; border: 0 ;">&nbsp;&nbsp;&nbsp;</td>
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
                                    <td style="background-color: #f2f2f2; border-left: 1px solid black;">${result[2]}</td>
                                    <td style="background-color: #f2f2f2; ">${result[3]}</td>
                                    <td style="background-color: #f2f2f2; "><#if (result[4])??> ${result[4]} <#else> - </#if></td>
                                    <td style="background-color: #00b050; width: 40px;">${result[5]}</td> 
                                    <td style="background-color: #ffff00; width: 40px;">${result[6]}</td> 
                                    <td style="background-color: #ffc000; width: 40px;">${result[7]}</td> 
                                    <td style="background-color: #ff0000; width: 40px;">${result[8]}</td> 
                                    <td style="background-color: #ffffff; border: 0;">&nbsp;&nbsp;&nbsp;</td>
                                    <td style="border-left: 1px solid black;border-right: 0;">${result[9]}</td>
                                    
                                    <#assign ind = 0>
                                       <#list result as resultData>
                                            <#if ind &gt; 9 && ind != (result?size - 1)>
                                                <td style="border-left: 1px solid black;border-right: 0;width: 45px;">${result[ind]}</td>
                                            </#if>
                                            <#assign ind = ind + 1>
                                        </#list>
                                    <td style="border-left:1px solid black; border-right: 1px solid black;">${result[result?size - 1]}</td>
                                </tr>
                                <#assign count = count + 1>
                            <#else>
                                <tr style="background-color:#fff2cc">
                                    <td style="background-color: #ffffff; border: 0 ;">${count}</td>
                                    <td style="background-color: #d9d9d9; border: 0 ;"><#if (result[0])??> ${result[0]} <#else> - </#if></td>
                                    <td style="background-color: #d9d9d9; border: 0 ;">${result[1]}</td>
                                    <td style="background-color: #ffffff; border: 0 ;">&nbsp;&nbsp;&nbsp;</td>
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
                                    <td style="background-color: #f2f2f2; border-bottom: 1px solid black; border-left: 1px solid black;">${result[2]}</td>
                                    <td style="background-color: #f2f2f2; border-bottom: 1px solid black; ">${result[3]}</td>
                                    <td style="background-color: #f2f2f2; border-bottom: 1px solid black; "><#if (result[4])??> ${result[4]} <#else> - </#if></td>
                                    <td style="background-color: #00b050; border-bottom: 1px solid black; width: 40px;">${result[5]}</td> 
                                    <td style="background-color: #ffff00; border-bottom: 1px solid black; width: 40px;">${result[6]}</td> 
                                    <td style="background-color: #ffc000; border-bottom: 1px solid black; width: 40px;">${result[7]}</td> 
                                    <td style="background-color: #ff0000; border-bottom: 1px solid black; width: 40px;">${result[8]}</td> 
                                    <td style="background-color: #ffffff; border: 0;">&nbsp;&nbsp;&nbsp;</td>
                                    <td style="border-left: 1px solid black; border-bottom: 1px solid black;border-right: 0;">${result[9]}</td>
                                    <#assign ind = 0>
                                    <#list result as resultData>
                                        <#if ind &gt; 9 && ind != (result?size - 1)>
                                            <td style="border-bottom: 1px solid black;border-left: 1px solid black;border-right: 0; width: 45px;">${result[ind]}</td>
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