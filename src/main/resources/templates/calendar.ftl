<!DOCTYPE html>
<html>
<head>
    <style>
        table {
            border-collapse: collapse;
            background: white;
            color: black;
            width: 100%;
        }

        th {
            color: white;
            background: purple;
            text-align: center;
            font-size: 300%;
            height: 240px;
        }

        td {
            text-align: center;
            font-size: 300%;
            font-weight: bold;
            height: 240px;
        }
    </style>
</head>
<body>
<h2 align="center" style="color: orange; text-align:center; font-size: 500%">
    ${calendar.monthName} ${calendar.year}

</h2>

<table bgcolor="lightgrey" align="center" cellspacing="21" cellpadding="21">

    <thead>
    <tr>
        <th>
            Mon
        </th>
        <th>
            Tue
        </th>
        <th>
            Wed
        </th>
        <th>
            Thu
        </th>
        <th>
            Fri
        </th>
        <th>
            Sat
        </th>
        <th>
            Sun
        </th>
    </tr>
    </thead>

    <tbody>
    <#list calendar.ratesToDays as week>
        <tr>
            <#list week as day>
                <td style="color: black; background: ${day.value};" align="center">${day.key}</td>
            </#list>
        </tr>
    </#list>
    </tbody>
</table>
</body>
</html>

