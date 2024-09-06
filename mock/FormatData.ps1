using namespace System.Text
using namespace System.Security.Cryptography
using namespace System.IO
using namespace System.Globalization
cls
cd $PSScriptRoot
$ErrorActionPreference = 'Stop'


# === SETTINGS ===
$formatUsers = $false
$formatShopItems = $true

# === FUNCTIONS ===
function Gen-Password(){
    Param(
        [int]$Length = 16
    )

    $chars = $([string]'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz123456789').ToCharArray()

    [string]$result = ""
    $rand = [System.Random]::new()
    for($i = 0; $i -lt $Length; $i++){
        $idx = $rand.Next(0, $chars.Length - 1)
        $result += $chars[$idx];
    }

    return $result
}
function Get-AuthHash(){
    Param(
        [Parameter(Mandatory = $true, ValueFromPipeline = $true)]
        [string]$Username,
        [Parameter(Mandatory = $true, ValueFromPipeline = $true)]
        [string]$Password
    )

    [SHA256]$hasher = [System.Security.Cryptography.HashAlgorithm]::Create('sha256')
    [byte[]]$block = [Encoding]::UTF8.GetBytes($Username + $Password)
    [byte[]]$hash = $hasher.ComputeHash($block)
    [string]$hashString = ""
    for($i = 0; $i -lt $hash.Count; $i++){
        $hashString += $hash[$i].ToString("X").PadLeft(2,'0')
    }
    return $hashString
}

# === SCRIPT ===
$numfmt = [CultureInfo]::InvariantCulture
if($formatUsers){
    $srcUsers = Get-Content -Path ".\raw\Users.json" | ConvertFrom-Json
    
    $dstUsers = @()
    $dstUserLogins = @()
    foreach($srcUser in $srcUsers){
    $dstUserLogin = [PsCustomObject]@{
        Username = $srcUser.email;
        Password = Gen-Password -Length 8
    }
    
    $dstUser = [PsCustomObject]@{
        FirstNames = $srcUser.first_names;
        LastName = $srcUser.last_name;
        Email = $srcUser.email;
        AuthHash = $($dstUserLogin | Get-AuthHash);
        Language = $srcUser.language;
    }
  
    $lastNameSpaceIdx = $dstUser.LastName.IndexOf(' ')
    if($dstUser.LastNameSpaceIdx -gt 0){
        $dstUser.FirstNames += ' ' + $dstUser.LastName.Substring($lastNameSpaceIdx).Trim()
        $dstUser.LastName = $dstUser.LastName.Substring(0, $lastNameSpaceIdx).Trim()
    }

    $dstUsers += $dstUser
    $dstUserLogins += $dstUserLogin
}
    
    $outString = $dstUsers | ConvertTo-Json -Compress
    $outPath = Join-Path -Path $PSScriptRoot -ChildPath "\real\users.json"
    [File]::WriteAllText($outPath, $outString, [Encoding]::UTF8)
    
    $outString = [string]::Join("`n", $($dstUserLogins | ConvertTo-Csv -Delimiter ';' -NoTypeInformation))
    $outPath = Join-Path -Path $PSScriptRoot -ChildPath "\real\userlogins.csv"
    [File]::WriteAllText($outPath, $outString, [Encoding]::UTF8)
    
    [System.GC]::Collect()
}

if($formatShopItems){
    $srcShopItems = Get-Content -Path ".\raw\ShopItem.json" | ConvertFrom-Json
    
    $categories = @{}   
    $sub_categories = @{}
    $cidx = 1
    $sidx = 1
    $dstShopItems = @()
    foreach($srcShopItem in $srcShopItems){
        $c = $srcShopItem.category
        $s = $srcShopItem.subcategory
        $cid = $null;
        $sid = $null;
        if(-not $categories.ContainsKey($c)){
            $cid = $cidx++;
            $categories[$c] = [PsCustomObject]@{
                Id = $cid;
                Name = $c;
            }
        }
        else {
            $cid = $categories[$c].Id
        }

        if(-not $sub_categories.ContainsKey($s)){
            $sid = $sidx++;
            $sub_categories[$s] = [PsCustomObject]@{
                Id = $sid;
                Name = $s;
                ParentId = $cid;
            }
        }
        else {
            $sid = $sub_categories[$s].Id
        }

        [double]$priceRound = [Math]::Round([Convert]::ToDouble($srcShopItem.price, $numfmt))

        $dstShopItems += [PsCustomObject]@{
            Name = $srcShopItem.name;
            Category = [int]$cid;
            Subcategory = [int]$sid;
            Description = [string]$srcShopItem.description;
            Price = $priceRound - 0.01;
            SalePrice = [Math]::Round($priceRound * 0.80) - 0.01;
        }
    }

    $outString = [string]::Join("`n",$($categories.Values | Sort-Object -Property Id | ConvertTo-Csv -Delimiter ';' -NoTypeInformation))
    $outPath = Join-Path -Path $PSScriptRoot -ChildPath "\real\categories.csv"
    [File]::WriteAllText($outPath, $outString, [Encoding]::UTF8)

    $outString = [string]::Join("`n",$($sub_categories.Values | Sort-Object -Property Id | ConvertTo-Csv -Delimiter ';' -NoTypeInformation))
    $outPath = Join-Path -Path $PSScriptRoot -ChildPath "\real\sub_categories.csv"
    [File]::WriteAllText($outPath, $outString, [Encoding]::UTF8)

    $outString = $dstShopItems | ConvertTo-Json -Compress
    $outPath = Join-Path -Path $PSScriptRoot -ChildPath "\real\shop_items.json"
    [File]::WriteAllText($outPath, $outString, [Encoding]::UTF8)

    [System.GC]::Collect()
}


