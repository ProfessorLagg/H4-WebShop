using namespace System.IO
using namespace System.Text

Param(
    [string]$rootUrl = "http://localhost:8080"
)

cd $PSScriptRoot
$ErrorActionPreference = 'Stop'

$rootUrl = $rootUrl.Trim();
if($rootUrl.EndsWith('/')){$rootUrl = $rootUrl.TrimEnd('/')}

$category_create_suburl = '/category'
$subcategory_create_suburl = '/subcategory'
$shopitem_create_suburl = '/shopitem'

$category_path = Join-Path -Path $PSScriptRoot -ChildPath '\real\categories.csv'
$subcategory_path = Join-Path -Path $PSScriptRoot -ChildPath '\real\sub_categories.csv'
$shopitem_path = Join-Path -Path $PSScriptRoot -ChildPath '\real\shop_items.json'

$categories = [File]::ReadAllText($category_path) | ConvertFrom-Csv -Delimiter ';'
$subcategories = [File]::ReadAllText($subcategory_path) | ConvertFrom-Csv -Delimiter ';'
$shopitems = [File]::ReadAllText($shopitem_path) | ConvertFrom-Json

Write-Host "posting categories"
$url = $rootUrl + $category_create_suburl
foreach($cat in $categories){
    $jsonString = $cat | ConvertTo-Json -Compress
    $response = Invoke-WebRequest -Uri $url -Method Post -DisableKeepAlive -ContentType 'application/json' -Body $jsonString
    Remove-Variable 'jsonString'
    Remove-Variable 'response'
}

Write-Host "posting subcategories"
$url = $rootUrl + $subcategory_create_suburl
foreach($sub in $subcategories){
    $jsonString = $sub | ConvertTo-Json -Compress
    $response = Invoke-WebRequest -Uri $url -Method Post -DisableKeepAlive -ContentType 'application/json' -Body $jsonString
    Remove-Variable 'jsonString'
    Remove-Variable 'response'
}

cls
Write-Host "posting shop items"
$url = $rootUrl + $shopitem_create_suburl
foreach($item in $shopitems){
    # Build JSON
    $jsonString = $item | ConvertTo-Json -Compress
    [byte[]]$jsonBytes = [Encoding]::UTF8.GetBytes($jsonString)
    # Build Request
    [System.Net.WebRequest]$request = [System.Net.WebRequest]::CreateHttp($url)
    $request.ContentType = 'application/json; charset=utf-8'
    $request.Method = 'POST'
    $request.body
    [Stream]$bodyWriteStream = $request.GetRequestStream()
    $bodyWriteStream.Write($jsonBytes, 0, $jsonBytes.Count)
    $bodyWriteStream.Close()
    # Send request
    $ErrorActionPreference = 'SilentlyContinue'
    $response = [System.Net.HttpWebResponse]$request.GetResponse()
    $ErrorActionPreference = 'Stop'
    if($null -eq $response){
        Write-Error "`$response was null"
    }elseif(($response.StatusCode -lt 200) -or ($response.StatusCode -gt 299)){
        Write-Error "$($response.Headers)`n`n$($response.Content)" -NoNewline
    }

    Remove-Variable 'jsonString'
    Remove-Variable 'jsonBytes'
    Remove-Variable 'request'
    Remove-Variable 'bodyWriteStream'
    Remove-Variable 'response'
}