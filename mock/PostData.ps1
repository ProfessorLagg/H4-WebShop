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
$lines = @()
foreach($item in $shopitems){
    # Build JSON
    $jsonString = $item | ConvertTo-Json -Compress
    $args = "-d '$jsonString' -H `"Content-Type: application/json`" -X POST `"$url`"" 
    $lines += "curl $args"
    Remove-Variable 'jsonString'
}

$outCmdPath = Join-Path -Path $PSScriptRoot -ChildPath "shopItems.cmd"
$outCmdContent = "@ECHO OFF`r`n" + "cd %~dp0`r`n" + [string]::Join("`r`n",$lines)
Set-Content -Path $outCmdPath -Value $outCmdContent