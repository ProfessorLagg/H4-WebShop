using namespace System.IO

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

foreach($cat in $categories){
    $jsonString = $cat | ConvertTo-Json -Compress
    $url = $rootUrl + $category_create_suburl
    $response = Invoke-WebRequest -Uri $url -Method Post -DisableKeepAlive -ContentType 'application/json' -Body $jsonString

}
