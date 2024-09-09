using namespace System.IO

cls
cd $PSScriptRoot
$ErrorActionPreference = 'Stop'

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

for($i = 0; $i -lt $products.Count; $i++){
    $product = $products[$i] | Select-Object id, title, price, description, category, image
    # image
    $imageUrl = $product.image
    $imageFilename = [Path]::GetFileName($imageUrl)
    $imageFilepath = Join-Path -Path $imageDirPath -ChildPath $imageFilename
    Invoke-WebRequest -Uri $imageUrl -OutFile $imageFilepath

    $product.image = $imageFilename
    
    $categoryName = $product.category
    $product.category = $categories | Where-Object {$_.Name -ieq $categoryName} #| Select-Object -ExpandProperty id

    

    $products[$i] = $product
}

$products | ConvertTo-Json -Compress -Depth 10 | Out-File -FilePath $productsFilePath -Encoding utf8 -Force
$categories | ConvertTo-Json -Compress | Out-File -FilePath $categoriesFilePath -Encoding utf8 -Force

# --- CREATE ---
$categoriesUrl = 'http://localhost:8080/category/'
$productsUrl = 'http://localhost:8080/product/'

$sqlLines = @()
foreach($c in $categories){
    $sqlLines += "INSERT INTO CATEGORY (ID, NAME) VALUES ($($c.id), '$($c.name.Replace("'","''"))');"
}


$numfmt = [System.Globalization.CultureInfo]::InvariantCulture
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

$sqlFilePath = Join-Path -Path $PSScriptRoot -ChildPath "insert.sql"
[File]::WriteAllLines($sqlFilePath, $sqlLines, [System.Text.Encoding]::UTF8)