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
            <div class="reportTitle">Client Wise Report</div>
            <div style="font-size:15px;">&nbsp;&nbsp;&nbsp;</div>
        </div>
        <#assign lastUpdated = .now>
        <div style="float:right;padding-bottom: 20px;">
            <span style="font-size: 10px;"> Last updated: ${lastUpdated?string.medium} </span>
        </div>
        <div style="padding: 1px;">
            <table width="100%" id="testTable">
                <thead>
                    <tr     style="background-color: #ffc000; color:#fff;height: 50px !important;">
                        <th style="background-color: #ffffff; width:30px; color:#000;">Sr. No</th>
                        <th style="background-color: #595959; width:60px;" >State Name</th>
                        <th style="background-color: #ffffff; border: 0; ">&nbsp;&nbsp;&nbsp;</th>
                        <th style="width:100px;">Total Volunteers</th>
                        <th style="width:58px; border-left: none; ">Total Users</th>
                        <th style="width:100px;">Total Citizens</th>
                        <th style="width:100px;">Total Voters</th>
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
                                    <td style="background-color:#fff;border: 0 ;">${count}</td>
                                    <td style="background-color:#d9d9d9;border: 0 ;"><#if (result[0])??> ${result[0]} <#else> N/A </#if></td>
                                    <td style="background-color:#fff;border: 0 ;">&nbsp;&nbsp;&nbsp;</td>
                                    <td style="border-left: 1px solid black;"><#if (result[1])??> ${result[1]} <#else> - </#if></td>
                                    <td>${result[2]}</td>
                                    <td>${result[3]}</td>
                                    <td style="border-right: 1px solid black;">${result[4]}</td>
                                </tr>
                                <#assign count = count + 1>
                            <#else>
                                <tr style="background-color:#fff2cc">
                                    <td style="background-color:#fff;border: 0 ;">${count}</td>
                                    <td style="background-color:#d9d9d9;border: 0 ;"><#if (result[0])??> ${result[0]} <#else> - </#if></td>
                                    <td style="background-color:#fff;border: 0 ;">&nbsp;&nbsp;&nbsp;</td>
                                    <td style="border-left: 1px solid black;border-bottom: 1px solid black;"><#if (result[1])??> ${result[1]} <#else> - </#if></td>
                                    <td style="border-bottom: 1px solid black;">${result[2]}</td>
                                    <td style="border-bottom: 1px solid black;">${result[3]}</td>
                                    <td style="border-bottom: 1px solid black;">${result[4]}</td>
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