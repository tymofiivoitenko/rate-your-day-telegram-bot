<!DOCTYPE html>
<html>
<head>
    <link href='https://fonts.googleapis.com/css?family=Montserrat' rel='stylesheet'/>
    <style>

        h2.month {
            color: orange;
            text-align: center;
            font-size: 700%;
            font-family: Montserrat, sans-serif;
        }
        table {
            border-collapse: collapse;
            background: white;
            color: black;
            table-layout: fixed;
            width: 100%;
            bgcolor: "lightgrey";
            cellspacing: 21;
            cellpadding: 21;
        }

        th {
            font-family: 'Montserrat';
            color: white;
            background: purple;
            text-align: center;
            font-size: 400%;
            height: 240px;
        }

        td {
            font-family: 'Montserrat';
            text-align: center;
            font-size: 400%;
            font-weight: bold;
            height: 240px;
        }
    </style>
</head>
<body>
<h2 class="month">
    ${monthName} ${year}

</h2>

<table>
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
    <#list view as week>
        <tr>
            <#list week as day>
                <td style="color: black; background: ${day.value}; test-align: center;">${day.key}</td>
            </#list>
        </tr>
    </#list>
    </tbody>
</table>
</body>
</html>

