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
        border: 1px solid #fff;
        font-size: 10px;
        text-align :center;
        padding: 1px;
        }
        
        td {
        border: 1px solid black;
        padding: 3px;
        font-size: 10px;
        text-align :center;
        border-bottom:0;
        border-left:0;
        }
        #testTable tr:last-child
        {
        border-bottom: 1px solid black;
        background-color: #FFFFFF;
        }
            
        .blank_row
        {
        height: 10px !important; /* overwrites any other rules */
        background-color: #FFFFFF;
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
        <div class="bold_header" style="padding-bottom: 10px;">
            <div class="reportTitle">Last Day Counting</div>
            <div class="reportTitle">${reportName}</div>
            <div style="font-size:15px;">${reportFor}</div>
        </div>
        <#assign lastUpdated = .now>
        <div style="float:right;padding-bottom: 20px;">
            <span style="font-size: 10px;"> Last updated: ${lastUpdated?string.medium} </span>
        </div>
        <div style="padding: 1px;">
            <table width="100%" id="testTable">
                <thead>
                    <tr>
                        <th colspan="5">&nbsp;</th>
                        <th colspan="${partyNames?size}" style="background: #a6a6a6; color:#fff;padding: 5px;">Survey Data</th>
                        <th colspan="${partyNames?size + 1}" style="background: #ffc000;  color:#fff;padding: 5px;">Live Voting </th>
                    </tr>
                    <tr style="background: #ffc000; color:#ffffff; height: 50px !important;">
                        <th style="background: #ffffff; color:#000;">Sr. No</th>
                        <th style="background-color: #a6a6a6; ">${tableFirstColumnName}</th>
                        <th style="background-color: #a6a6a6; ">Total Voters</th>
                        <th>Voters Voted</th>
                        <th style="background-color: #a6a6a6; color:#ffffff; ">% of total Voters</th>
                        <#list partyNames as party>
                        	<th style="background-color: #a6a6a6; width:45px;">${party}</th>
                        </#list>
                        <#list partyNames as party>
                        	<th style=" width:45px;">${party}</th>
                        </#list>
                        <th style=" width:45px;">Not Disclosed</th>
                    </tr>
                </thead>
                <tbody>
                    <tr class="blank_row">
                        <td style="border: 0 "></td>
                    </tr>
                                        
                    <#if reportData??>
                        <#assign count = 1>
                        <#list reportData as result>
                            <#if count != (reportData?size)>
                                <tr style="background-color:#fff2cc">
                                    <td style="background-color: #ffffff; border-left: 1px solid black;">${count}</td>
                                    <td style="background-color: #a6a6a6;"><#if (result[0])??> ${result[0]} <#else> - </#if></td>
                                    <td style="background-color: #a6a6a6;"><#if (result[1])??> ${result[1]} <#else> - </#if></td>
                                    <td><#if (result[2])??> ${result[2]} <#else> - </#if></td>
                                    <td style="background-color: #a6a6a6;"><#if (result[3])??> ${result[3]} <#else> - </#if></td>
                                    <#assign ind = 0>
                                    <#list result as resultData>
                                        <#if ind &gt; 3 && ind &lt; partyNames?size + 4 && ind != (result?size - 1)>
                                            <td style="background-color: #a6a6a6;"><#if (result[ind])??> ${result[ind]} <#else> - </#if></td>
                                        </#if>
                                        <#assign ind = ind + 1>
                                    </#list>
                                    <#assign indx = 0>
                                    <#list result as resultData>
                                        <#if indx &gt; partyNames?size + 3 && indx != (result?size - 1)>
                                            <td ><#if (result[indx])??> ${result[indx]} <#else> - </#if></td>
                                        </#if>
                                        <#assign indx = indx + 1>
                                    </#list>
<!--                                    <td style="border: 1px solid white; background-color: #000000; color:#ffffff; ">${result[result?size - 5]}</td>
                                    <td style="border: 1px solid white; background-color: #000000; color:#ffffff; ">${result[result?size - 4]}</td>
                                    <td style="border: 1px solid white; background-color: #000000; color:#ffffff; ">${result[result?size - 3]}</td>
                                    <td style="border: 1px solid white; background-color: #000000; color:#ffffff; ">${result[result?size - 2]}</td>-->
                                    <td style="border-right: 1px solid black;">${result[result?size - 1]}</td>
                                </tr>
                                <#assign count = count + 1>
                            <#else>
                                <tr style="background-color:#fff2cc">
                                    <td style="background-color:#ffffff ;border-left: 1px solid black; border-bottom: 1px solid black;">${count}</td>
                                    <td style="background-color: #a6a6a6; border-bottom: 1px solid black;"><#if (result[0])??> ${result[0]} <#else> - </#if></td>
                                    <td style="background-color: #a6a6a6; border-bottom: 1px solid black;"><#if (result[1])??> ${result[1]} <#else> - </#if></td>
                                    <td style="border-bottom: 1px solid black;"><#if (result[2])??> ${result[2]} <#else> - </#if></td>
                                    <td style="background-color: #a6a6a6; border-bottom: 1px solid black; "><#if (result[3])??> ${result[3]} <#else> - </#if></td>                                    
                                    <#assign ind = 0>
                                    <#list result as resultData>
                                        <#if ind &gt; 3 && ind &lt; partyNames?size + 4 && ind != (result?size - 1)>
                                            <td style="border-bottom: 1px solid black;background-color: #a6a6a6;"><#if (result[ind])??> ${result[ind]} <#else> - </#if></td>
                                        </#if>
                                        <#assign ind = ind + 1>
                                    </#list>
                                    <#assign indx = 0>
                                    <#list result as resultData>
                                        <#if indx &gt; partyNames?size + 3 && indx != (result?size - 1)>
                                            <td style="border-bottom: 1px solid black;"><#if (result[indx])??> ${result[indx]} <#else> - </#if></td>
                                        </#if>
                                        <#assign indx = indx + 1>
                                    </#list>                                    
<!--                                    <td style="border: 1px solid white; border-bottom: 1px solid black; background-color: #000000; color:#ffffff; ">${result[result?size - 5]}</td>
                                    <td style="border: 1px solid white; border-bottom: 1px solid black; background-color: #000000; color:#ffffff; ">${result[result?size - 4]}</td>
                                    <td style="border: 1px solid white; border-bottom: 1px solid black; background-color: #000000; color:#ffffff; ">${result[result?size - 3]}</td>
                                    <td style="border: 1px solid white; border-bottom: 1px solid black; background-color: #000000; color:#ffffff; ">${result[result?size - 2]}</td>-->
                                    <td style="border-bottom: 1px solid black; ">${result[result?size - 1]}</td>
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