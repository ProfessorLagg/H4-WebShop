using namespace System.IO
using namespace System.Text

cls
cd $PSScriptRoot
$ErrorActionPreference = 'Stop'

function ConvertTo-HexString(){
    Param(
        [Parameter(Mandatory = $true, ValueFromPipeline = $true)]
        [byte[]]$bytes
    )

    [string]$str = ""
    for($i = 0; $i -lt $bytes.Length; $i++){
        $str += $bytes[$i].ToString("X").PadLeft(2,'0')
    }
    return $str
}

function Escape-SqlString(){
    Param(
        [Parameter(Mandatory = $true, ValueFromPipeline = $true)]
        [string]$string
    )
    [string]$result = "" + $string;
    
    $result = $result.Replace("" + [char]00, '¤0')
    $result = $result.Replace("'","''")
    $result = $result.Replace('"','""')
    $result = $result.Replace("" + [char]08,'¤b') # backspace
    $result = $result.Replace("`n",'¤n')
    $result = $result.Replace("`r",'¤r')
    $result = $result.Replace("`t",'¤t')
    $result = $result.Replace("" + [char]26,'¤Z')
    $result = $result.Replace('\','¤\')
    $result = $result.Replace('%','¤%')
    $result = $result.Replace('_','¤_')
    return $result.Replace('¤','\')
}

# --- SCRAPE ---
$categoriesUrl = 'https://fakestoreapi.com/products/categories'
$categoriesFilePath = Join-Path -Path $PSScriptRoot -ChildPath "\data\categories.json"
$rawcategories = Invoke-WebRequest -Uri $categoriesUrl | Select-Object -ExpandProperty Content | ConvertFrom-Json
$categories = @()
for($i = 0; $i -lt $rawcategories.Count; $i++){
    $categories += [PsCustomObject]@{
        id = $i + 1;
        name = $rawcategories[$i]
    }
}

$productsUrl = 'https://fakestoreapi.com/products'
$productsFilePath = Join-Path -Path $PSScriptRoot -ChildPath "\raw\products.json"
$products = Invoke-WebRequest -Uri $productsUrl | Select-Object -ExpandProperty Content | ConvertFrom-Json


$imageDirPath = Join-Path -Path $PSScriptRoot -ChildPath "\raw\img"
[Directory]::CreateDirectory($imageDirPath) | Out-Null
Get-ChildItem -Path $imageDirPath | Remove-Item -Force | Out-Null
$images = @()

for($i = 0; $i -lt $products.Count; $i++){
    $product = $products[$i] | Select-Object id, title, price, description, category, image
    # image
    $imageUrl = $product.image
    $imageFilename = [Path]::GetFileName($imageUrl);
    $imageFilepath = Join-Path -Path $imageDirPath -ChildPath $imageFilename
    Invoke-WebRequest -Uri $imageUrl -OutFile $imageFilepath

    $imageFileHash = Get-FileHash -Path $imageFilepath -Algorithm MD5
    $imageNewName = $imageFileHash.Hash + [Path]::GetExtension($imageUrl)
    $imageNewPath = Join-Path -Path $imageDirPath -ChildPath $imageNewName
    [File]::Move($imageFilepath, $imageNewPath)


    $images += [PsCustomObject]@{
        name = $imageNewName;
        data = [File]::ReadAllBytes($imageNewPath);
        filepath = $imageNewPath
    }

    $product.image = $imageNewName
    
    $categoryName = $product.category
    $product.category = $categories | Where-Object {$_.Name -ieq $categoryName} #| Select-Object -ExpandProperty id

    

    $products[$i] = $product
}

$products | ConvertTo-Json -Compress -Depth 10 | Out-File -FilePath $productsFilePath -Encoding utf8 -Force
$categories | ConvertTo-Json -Compress | Out-File -FilePath $categoriesFilePath -Encoding utf8 -Force

# --- SQL ---
$numfmt = [System.Globalization.CultureInfo]::InvariantCulture

$sqlLines = @()
foreach($c in $categories){
    $sqlLines += "INSERT INTO CATEGORY (ID, NAME) VALUES ($($c.id), '$($c.name.Replace("'","''"))');"
}

$sqlLines += "TRUNCATE TABLE PRODUCT;"
foreach($p in $products){
    $value = @(
        $p.id.ToString('0'), # ID
        "'$($p.description | Escape-SqlString)'", # DESCRIPTION
        "'$($p.image)'", # IMAGE
        $p.price.ToString('n', $numfmt), # PRICE
        "'$($p.title | Escape-SqlString)'", # TITLE
        $p.category.id.ToString() # CATEGORY_ID
    )
    $sqlLines += "INSERT INTO PRODUCT (ID, DESCRIPTION, IMAGE, PRICE, TITLE, CATEGORY_ID) VALUES (" + [string]::Join(', ',$value) + ");"
}

$sqlLines += "TRUNCATE TABLE IMAGE;"
foreach($img in $images){
        $value = @(
        "'$($img.name | Escape-SqlString)'",
        "null"
    )
    $sqlLines += "INSERT INTO IMAGE (NAME, DATA) VALUES (" + [string]::Join(', ',$value) + ");"
}


$sqlFilePath = Join-Path -Path $PSScriptRoot -ChildPath "insert.sql"
[File]::WriteAllLines($sqlFilePath, $sqlLines, [System.Text.Encoding]::UTF8)

# --- POST ---
$imageUrlTmpl = 'http://localhost:8080/img/{name}'
foreach($img in $images){
    $url = $imageUrlTmpl.Replace("{name}", [System.Web.HttpUtility]::UrlEncode($img.name))
    $base64 = [Convert]::ToBase64String([File]::ReadAllBytes($img.filepath))
    Invoke-RestMethod -Uri $url -Method Post -body $base64 -ContentType 'text/plain'
}
