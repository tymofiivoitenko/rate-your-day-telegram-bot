<!DOCTYPE html>
<html>
<head>
    <style type="text/css">
        <#include "css/style.css">
    </style>
</head>
<body>
<h2 class="month">
    ${monthName} ${year}

</h2>

<table>
    <thead>
    <tr>
        <th>Mon</th>
        <th>Tue</th>
        <th>Wed</th>
        <th>Thu</th>
        <th>Fri</th>
        <th>Sat</th>
        <th>Sun</th>
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

