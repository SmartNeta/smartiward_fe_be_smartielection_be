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
            <div class="reportTitle">Surveyed Data</div>
            <div class="reportTitle">Booth Report</div>
            <div style="font-size:15px;">Booth Number: ${boothNumber}</div>
        </div>
        <#assign lastUpdated = .now>
        <div style="float:right;padding-bottom: 20px;">
            <span style="font-size: 10px;"> Last updated: ${lastUpdated?string.medium} </span>
        </div>
        <div style="padding: 1px;">
            <table width="100%" id="testTable">
                <thead>
                    <tr>
                        <th colspan="6">&nbsp;</th>
                        <th colspan="4" style="background: #ffc000;color:#fff;padding: 5px;">Voters gradation</th>
                        <th colspan="${totalParties}" style="background: #ffc000;color:#fff;padding: 5px;">Voters Responded</th>
                    </tr>
                    <tr style="background: #ffc000;color:#fff;height: 50px !important;">
                        <th style="background: #fff;color:#000;">Sr. No</th>
                        <th style="background-color: #595959;" >Booth #</th>
                        <th style="background-color: #ffe699; border-right: none; color: black;">Volunteer Mobile #</th>
                        <th style="border: 0 ; background-color: #fff;">&nbsp;&nbsp;&nbsp;</th>
                        <th style="border-left: none; ">Voter Visited</th>
                        <th style="width:58px;">Voters Responded</th>
                        <th style="width:40px;">${segmentations[0].label} </th>
                        <th style="width:40px;">${segmentations[1].label} </th>
                        <th style="width:40px;">${segmentations[2].label} </th>
                        <th style="width:40px;">${segmentations[3].label} </th>
                        
                        <#list partyNames as party>
                        <th style="width:45px;">${party}</th>
                        </#list>
                        
                        <th>Not disclosed</th>
                    </tr>
                </thead>
                <tbody>
                    <tr class="blank_row">
                        <td style="border: 0 "></td>
                    </tr>
                    <#if overallResult??>
                    <tr style="font-weight: bold;background-color: #fff2cc;height: 50px !important;">
                        <td colspan="4" style="font-size: 14px !important;border: 1px solid #fff;">Overall Result</td>
                        <#list overallResult as overallResultObject>
                        <td style="border: 1px solid #fff;">${overallResultObject}</td>
                        </#list>
                    </tr>
                    </#if>
                    <tr class="blank_row">
                        <td style="border: 0 !important;"></td>
                    </tr>
                                        
                    <#if reportData??>
                        <#assign count = 1>
                        <#list reportData as result>
                            <#if count != (reportData?size)>
                                <tr style="background-color:#fff2cc">
                                    <td style="background-color:#fff;border: 0 ;">${count}</td>
                                    <td style="background-color:#d9d9d9;border: 0 ;">${result[0]}</td>
                                    <td style="background-color:#f2f2f2;border: 0 ;"><#if (result[1])??> ${result[1]} <#else>N/A</#if></td>
                                    <td style="background-color:#fff;border: 0 ;">&nbsp;&nbsp;&nbsp;</td>
                                    <td style="border-left: 1px solid black;">${result[2]}</td>
                                    <#assign ind = 0>
                                    <#list result as resultData>
                                        <#if ind &gt; 2 && ind != (result?size - 1)>
                                            <td>${result[ind]}</td>
                                        </#if>
                                        <#assign ind = ind + 1>
                                    </#list>
                                    <td style="border-right: 1px solid black;">${result[result?size - 1]}</td>
                                </tr>
                                <#assign count = count + 1>
                            <#else>
                                <tr style="background-color:#fff2cc">
                                    <td style="background-color:#fff;border: 0 ;">${count}</td>
                                    <td style="background-color:#d9d9d9;border: 0 ;">${result[0]}</td>
                                    <td style="background-color:#f2f2f2;border: 0 ;"><#if (result[1])??> ${result[1]} <#else>N/A</#if></td>
                                    <td style="background-color:#fff;border: 0 ;">&nbsp;&nbsp;&nbsp;</td>
                                    <td style="border-left: 1px solid black;border-bottom: 1px solid black;">${result[2]}</td>
                                    <#assign ind = 0>
                                        <#list result as resultData>
                                            <#if ind &gt; 2 && ind != (result?size - 1)>
                                                <td style="border-bottom: 1px solid black;">${result[ind]}</td>
                                            </#if>
                                            <#assign ind = ind + 1>
                                        </#list>
                                    <td style="border-bottom: 1px solid black;">${result[result?size - 1]}</td>
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